package logic.server;

import logic.controllers.*;
import logic.dao.*;
import logic.interfaces.Subject;
import logic.model.Message;
import logic.model.Notification;
import logic.utils.*;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server implements Subject {
    private Map<String, List<ObserverClass>> observersByCity = new HashMap<>();
    private Map<Integer, ObserverClass> organizersByEventID = new HashMap<>();
    private Map<Integer, Boolean> connectedUsers = new HashMap<>();
    private Map<Integer, Boolean> connectedOrganizers = new HashMap<>();
    private Map<Integer, List<ObserverClass>> usersInGroups = new HashMap<>();

    private static Logger logger = Logger.getLogger("NightPlan");
    private int connections = 0;
    private static final int MAX_CONNECTIONS = 500;
    private ServerSocket serverSocket;
    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;
    private NotificationFactory notiFactory;
    private ObserverFactory obsFactory;
    private static final SituationType SERVER_CLIENT = SituationType.ServerClient;

    public Server() {
        notiFactory = new NotificationFactory();
        obsFactory = new ObserverFactory();
    }

    public static void main(String[] args) {
        logger.info("Server running on port " + PORT);

        Server server = new Server();
        server.start();
    }

    private void start() {
        /*preload all'avvio dal DB delle hashmap del server
        (il server è un controller a tutti gli effetti,
        facciamo questa operazione solo per semplicità
        perché il nostro server non rimane sempre accesso 24/7 */

        loadData();
        boolean serverRunning = true;

        try {
            //attivo la connessione del server tramite ServerSocket
            serverSocket = new ServerSocket(PORT);
            while (serverRunning) {
                //verifico che non si sia raggiunto il numero massimo di connessioni possibili al server
                if (connections == MAX_CONNECTIONS) {
                    serverRunning = false;
                    continue;
                }

                //aspetto che un utente si connetta da client al server aprendo una Socket
                Socket client = serverSocket.accept();

                //aggiungo nuovo utente al numero di connessioni
                connections++;

                logger.info("Nuovo client connesso: " + client.getInetAddress() + " on port " + client.getPort());
                ClientHandler ch = new ClientHandler(client);
                //avvio un thread apposito per ogni client che si connette che dovrà gestire tutti gli scambi di dati tra client-server
                Thread t = new Thread(ch);
                t.start();
            }
        } catch (IOException | IllegalThreadStateException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }



    private class ClientHandler implements Runnable {
        private Socket client;
        private SecureObjectInputStream in;
        private ObjectOutputStream out;

        private boolean clientRunning = true;

        public ClientHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            try {
                //quando viene avviato il thread associato al client parte questa funzione run()
                //apriamo i canali di input/output del client con il server.
                out = new ObjectOutputStream(client.getOutputStream()); //da server a client
                in = new SecureObjectInputStream(client.getInputStream()); //da client a server
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                while (clientRunning) { //questo ciclo while si interrompe non appena il Client chiude i suoi canali di comunicazione con il server
                    //blocco il thread in lettura di un messaggio in arrivo dal client
                    Object object = in.readObject();
                    if (object instanceof Notification) {
                        handleNotification((Notification) object);
                    } else if (object instanceof Message) {
                        handleMessage((Message) object);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                //gestione eccezioni di deserializzazione (readObject)
                logger.severe(e.getMessage());
            }
        }

        private void handleMessage(Message mex) {
            //implementato solo il caso di group message quindi il message type verra' ignorato
            //rigiro il messaggio cosi' come e' arrivato al server
            System.out.println("Ricevuto messaggio da id: " + mex.getSenderID() + ", verso group id: " + mex.getReceiverID() + ", testo: " + mex.getMessage());
            //notifyGroupUsers(mex.getReceiverID(), mex);
            Server.this.notify(SubjectTypes.UsersInGroup,mex);
            //notifico solo gli utenti online nel gruppo con quell'ID (receiverID)


        }

        private void handleNotification(Notification noti) {
            Notification response;
            switch (noti.getNotificationType()) {
                case UserRegistration:
                    System.out.println("User registered, id = " + noti.getClientID() + ", city = " + noti.getCity());
                    synchronized (observersByCity) {
                        //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                        //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                        if(!attach(SubjectTypes.UsersInCity,noti, null)){
                            logger.severe("Error during UserRegistration attach");
                        }
                    }
                    //notifica l'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserRegistration, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);
                    connections--;
                    clientRunning = false;
                    break;

                case LoggedIn:
                    switch (noti.getUserType()) {
                        case USER:
                            System.out.println("User logged in, id = " + noti.getClientID());
                            synchronized (connectedUsers) {
                                updateLoggedUsers(noti.getClientID(), true, noti.getUserType());
                            }
                            synchronized (observersByCity) {
                                updateUserOut(noti.getCity(), noti.getClientID(), out);
                            }
                            break;

                        case ORGANIZER:
                            System.out.println("Organizer logged in, id = " + noti.getClientID());
                            synchronized (connectedOrganizers) {
                                updateLoggedUsers(noti.getClientID(), true, noti.getUserType());
                            }
                            synchronized (organizersByEventID) {
                                updateOrgOut(noti.getClientID(), out);
                            }
                            break;
                    }

                    //notifica l'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.LoggedIn, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);
                    break;

                case EventAdded:
                    System.out.println("New event added by " + noti.getClientID() + ", eventID: " + noti.getEventID() + ", city: " + noti.getCity());
                    synchronized (organizersByEventID) {
                        //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                        //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                        if(!attach(SubjectTypes.UsersInCity, noti, out)){
                            logger.severe("Error during EventAdded attach");
                        }
                    }
                    //notifica l'organizer
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EventAdded, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);

                    //notifica l'utente
                    if(!Server.this.notify(SubjectTypes.UsersInCity,noti)){
                        logger.severe("Error during EventAdded notify");
                    }
                    break;

                case EventDeleted:
                    System.out.println("Event with id " + noti.getEventID() + " deleted");
                    synchronized (organizersByEventID) {
                        //rimuove l'associazione tra event-id e organizer nella hashmap
                        if(!detach(SubjectTypes.EventOrganizer,noti)){
                            logger.severe("Error during EventDeleted detach");
                        }
                    }
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EventDeleted, null, null, noti.getEventID(), null, null, null, null);
                    sendNotificationToClient(response, out);
                    break;

                case UserEventParticipation:
                    System.out.println("User with id " + noti.getClientID() + " participating to event with id = " + noti.getEventID());

                    //mandare messaggio di ritorno all'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserEventParticipation, null, null, null, null, null, null, null);
                    sendNotificationToClient(response, out);

                    //mandare notifica all'organizzatore
                    if(!Server.this.notify(SubjectTypes.EventOrganizer,noti)){
                        logger.severe("Error during UserEventParticipation notify");
                    }

                    break;

                case UserEventRemoval:
                    System.out.println("User with id " + noti.getClientID() + " removed participation to event with id = " + noti.getEventID());

                    //mandare messaggio di ritorno all'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserEventRemoval, noti.getClientID(), null, noti.getEventID(), null, null, null, null);
                    sendNotificationToClient(response, out);
                    break;

                case ChangeCity:
                    System.out.println("User with id = " + noti.getClientID() + ", changed city from " + noti.getCity() + " to " + noti.getNewCity());

                    synchronized (observersByCity) {
                        //DETACH CITY VECCHIA
                        if (detach(SubjectTypes.UsersInCity,noti)) {
                            System.out.println("User with id " + noti.getClientID() + " detached from city " + noti.getCity());
                        } else {
                            logger.severe("Error during ChangeCity detach");
                        }
                        //ATTACH SU CITY NUOVA
                        Notification newNoti = notiFactory.createNotification(SERVER_CLIENT, null, noti.getClientID(), null, null, null, noti.getNewCity(), null, null);
                        if(!attach(SubjectTypes.UsersInCity,newNoti, out)){
                            logger.severe("Error during ChangeCity attach");
                        }
                    }

                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.ChangeCity, noti.getClientID(), null, null, null, noti.getCity(), noti.getNewCity(), null);
                    sendNotificationToClient(response, out);
                    break;

                case GroupJoin:
                    System.out.println("User with id = " + noti.getClientID() + ", joined group with id = " + noti.getEventID()); //event viene usato come group

                    //AGGIORNO HASHMAP
                    synchronized (usersInGroups) {
                        if(!attach(SubjectTypes.UsersInGroup,noti, out)){
                            logger.severe("Error during GroupJoin attach");
                        }
                    }

                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GroupJoin, null, null, noti.getEventID(), null, null, null, null);
                    sendNotificationToClient(response, out);
                    break;

                case GroupLeave:
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GroupLeave, null, null, noti.getEventID(), null, null, null, null);

                    synchronized (usersInGroups) {
                        if (detach(SubjectTypes.UsersInGroup,noti)) {
                            System.out.println("User with id = " + noti.getClientID() + ", left group with id = " + noti.getEventID());
                        }else{
                            logger.severe("Error during GroupLeave detach");
                        }
                    }

                    sendNotificationToClient(response, out);
                    break;

                case Disconnected:
                    switch (noti.getUserType()) {
                        case USER:
                            System.out.println("User logged out, id = " + noti.getClientID());
                            synchronized (connectedUsers) {
                                updateLoggedUsers(noti.getClientID(), false, noti.getUserType());
                            }
                            break;
                        case ORGANIZER:
                            System.out.println("Organizer logged out, id = " + noti.getClientID());
                            synchronized (connectedOrganizers) {
                                updateLoggedUsers(noti.getClientID(), false, noti.getUserType());
                            }
                            break;
                    }
                    //notifica l'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.Disconnected, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);
                    connections--;
                    clientRunning = false;
                    break;
            }
        }
    }


    private void loadData() {
        try {
            UserDAO userDAO = new UserDAO();
            EventDAO eventDAO = new EventDAO();
            GroupDAO groupDAO = new GroupDAO();
            userDAO.populateObsByCity(this.observersByCity);
            userDAO.populateConnUsers(this.connectedUsers);
            userDAO.populateConnOrganizers(this.connectedOrganizers);
            eventDAO.populateOrgByEventID(this.organizersByEventID);
            groupDAO.populateUsersInGroups(this.usersInGroups);
        } finally {
            logger.info("Finished to preload data from database");
        }
    }

    private void sendNotificationToClient(Notification noti, ObjectOutputStream out) {
        try {
            out.writeObject(noti);
            out.flush();
            out.reset();
        } catch (InvalidClassException e) {
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object noti");
        } catch (IOException e) {
            //gestione eccezioni IO
            logger.severe(e.getMessage());
        }
    }

    private void updateUserOut(String city, int clientID, ObjectOutputStream out) {
        //aggiorna canale di output nella hashmap delle citta'
        List<ObserverClass> users = observersByCity.get(city);
        for (ObserverClass user : users) {
            if (user.getObsID() == clientID) {
                user.setOut(out);
            }
        }
        synchronized (usersInGroups) {
            //aggiorna canale di output nell'hashmap dei gruppi
            for (Map.Entry<Integer, List<ObserverClass>> entry : usersInGroups.entrySet()) {
                for (ObserverClass user : entry.getValue()) {   //entry.getValue() mi restituisce una lista di observers
                    if (user.getObsID() == clientID) {
                        user.setOut(out);
                    }
                }
            }
        }
    }

    //TODO: sistemare interfaccia Subject con attach, detach e notify da fixare

    private void updateOrgOut(int orgID, ObjectOutputStream out) {
        for (Map.Entry<Integer, ObserverClass> entry : organizersByEventID.entrySet()) {
            if (entry.getValue().getObsID() == orgID) {
                entry.getValue().setOut(out);
            }
        }
    }

    private void updateLoggedUsers(int userID, boolean isConnected, UserTypes type) {
        switch (type) {
            case USER:
                connectedUsers.put(userID, isConnected);
                System.out.println("User id: " + userID + ", isConnected:" + connectedUsers.get(userID).toString());
                break;
            case ORGANIZER:
                connectedOrganizers.put(userID, isConnected);
                System.out.println("Organizer id: " + userID + ", isConnected:" + connectedOrganizers.get(userID).toString());
                break;
        }
    }

    @Override
    public boolean attach(SubjectTypes type, Notification noti, ObjectOutputStream out) {
        switch (type){
            case UsersInCity:
                return attachUserToCity(noti,out);
            case EventOrganizer:
                return attachOrgToEvent(noti,out);
            case UsersInGroup:
                return attachUserToGroup(noti, out);
        }
        return false;
    }

    @Override
    public boolean detach(SubjectTypes type, Notification noti) {
        switch (type){
            case UsersInCity:
                return detachUserFromCity(noti);
            case EventOrganizer:
                return detachOrgFromEvent(noti);
            case UsersInGroup:
                return detachUserFromGroup(noti);
        }
        return false;
    }

    @Override
    public boolean notify(SubjectTypes type, Object o) {
        switch (type){
            case UsersInCity:
                return notifyUsersInCity((Notification) o);
            case EventOrganizer:
                return notifyEventOrg((Notification) o);
            case UsersInGroup:
                return notifyUsersInGroup((Message) o);
        }
        return false;
    }

    //ATTACH
    private boolean attachOrgToEvent(Notification noti, ObjectOutputStream out) {
        if (noti==null){
            return false;
        }
        ObserverClass orgObs = obsFactory.createObserver(ObserverType.NotiObserver, noti.getClientID(), out);
        organizersByEventID.put(noti.getEventID(), orgObs);
        System.out.println("Added eventID: " + noti.getEventID() + " to orgID: " + orgObs.getObsID());
        return true;
    }

    private boolean attachUserToCity(Notification noti, ObjectOutputStream out) {
        if (noti==null){
            return false;
        }
        ObserverClass notiObs = obsFactory.createObserver(ObserverType.NotiObserver, noti.getClientID(), out);
        observersByCity.computeIfAbsent(noti.getCity(), k -> new ArrayList<>()).add(notiObs);
        System.out.println("Added userID " + notiObs.getObsID() + " to city: " + noti.getCity());
        return true;
    }

    private boolean attachUserToGroup(Notification noti, ObjectOutputStream out) {
        if (noti==null){
            return false;
        }
        Integer groupID = noti.getEventID();
        ObserverClass groupObs = obsFactory.createObserver(ObserverType.MessageObserver, noti.getClientID(), out);
        usersInGroups.computeIfAbsent(groupID, k -> new ArrayList<>()).add(groupObs);
        System.out.println("Added userID: " + groupObs.getObsID() + " to groupID: " + groupID);
        return true;
    }

    //DETACH
    private boolean detachUserFromCity(Notification noti) {
        Integer userID = noti.getClientID();
        String city = noti.getCity();
        List<ObserverClass> list = observersByCity.get(city);
        if (list != null) {
            list.removeIf(obs -> obs.getObsID() == userID);
            System.out.println("Detached user id " + userID + " from city " + city);
            return true;
        }
        return false;
    }

    private boolean detachOrgFromEvent(Notification noti) {
        if (noti==null){
            return false;
        }
        ObserverClass tempObs = organizersByEventID.remove(noti.getEventID());
        System.out.println("Removed eventID: " + noti.getEventID() + " to orgID: " + tempObs.getObsID());
        return true;
    }

    private boolean detachUserFromGroup(Notification noti) {
        Integer groupID = noti.getEventID();
        List<ObserverClass> tempList = usersInGroups.get(groupID);
        if (tempList != null) {
            tempList.removeIf(obs -> obs.getObsID() == noti.getClientID());
            System.out.println("Removed userID: " + noti.getClientID() + " from groupID: " + groupID);
            return true;
        }
        return false;
    }

    private boolean notifyUsersInCity(Notification noti) {
        List<ObserverClass> users = observersByCity.get(noti.getCity());
        if (users != null) {
            for (ObserverClass user : users) {
                if (connectedUsers.get(user.getObsID())) {
                    //se utente online notifica
                    user.update(noti);
                }
            }
            return true;
        }
        return false;
    }

    private boolean notifyEventOrg(Notification noti) {
        ObserverClass org = organizersByEventID.get(noti.getEventID());
        if (org == null) {
            logger.severe("Org id got from observer is null - can't update observers");
            throw new RuntimeException();

        }
        if (connectedOrganizers.get(org.getObsID())) {
            //se org online notifica
            org.update(noti);
        }
        return true;
    }

    private boolean notifyUsersInGroup(Message mex) {
        List<ObserverClass> usersObsList = usersInGroups.get(mex.getReceiverID());
        if (usersObsList != null) {
            for (ObserverClass obs : usersObsList) {
                if (connectedUsers.get(obs.getObsID())) {
                    //se user online mandagli il messaggio per aggiungerlo alla chat
                    obs.update(mex);
                }
            }

        } else {
            logger.severe("Org id got from observer is null - can't update observers");
            throw new RuntimeException();
        }
        return true;
    }

}

