package logic.server;

import logic.controllers.MessageFactory;
import logic.controllers.MessageObserverClass;
import logic.controllers.NotificationFactory;
import logic.controllers.NotiObserverClass;
import logic.dao.*;
import logic.model.Message;
import logic.model.Notification;
import logic.model.ServerNotification;
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


public class Server {
    private Map<String, List<NotiObserverClass>> observersByCity = new HashMap<>();
    private Map<Integer, NotiObserverClass> organizersByEventID = new HashMap<>();
    private Map<Integer, Boolean> connectedUsers = new HashMap<>();
    private Map<Integer, Boolean> connectedOrganizers = new HashMap<>();
    private Map<Integer, List<MessageObserverClass>> usersInGroups = new HashMap<>();

    private static Logger logger = Logger.getLogger("NightPlan");
    private static final int MAX_CONNECTIONS = 500;
    private ServerSocket serverSocket;

    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;
    private NotificationFactory notiFactory;
    private MessageFactory msgFactory;
    private NotificationDAO notifyDAO;
    private static final SituationType SERVER_CLIENT = SituationType.ServerClient;
    private static final SituationType LOCAL = SituationType.Local;

    public Server() {
        notiFactory = new NotificationFactory();
        msgFactory = new MessageFactory();
        notifyDAO = new NotificationDAO();
    }


    public static void main(String[] args) {
        logger.info("Server running on port " + PORT);

        if (args.length > 0) {
            if ("JDBC".equals(args[0])) {
                logger.info("Server started with JDBC persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
            } else if ("FileSystem".equals(args[0])) {
                logger.info("Server started with FileSystem persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.FileSystem);
            }
        } else {
            logger.info("Server started with default persistence logic (JDBC)");
            PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
        }

        Server server = new Server();
        server.start();
    }

    private void start() {
        /*preload all'avvio dal DB delle hashmap del server
        (il server è un controller a tutti gli effetti,
        facciamo questa operazione solo per semplicità
        perché il nostro server non rimane sempre accesso 24/7 */

        loadData();

        int connections = 0;
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
                    if(object instanceof Notification){
                        handleNotification((ServerNotification) object);
                    } else if (object instanceof Message){
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
            System.out.println("Ricevuto messaggio da id: "+mex.getSenderID()+", verso group id: "+mex.getReceiverID()+", testo: "+mex.getMessage());
            notifyGroupUsers(mex.getReceiverID(),mex);
            //notifico solo gli utenti online nel gruppo con quell'ID (receiverID)


        }

        private void handleNotification(ServerNotification noti) {
            Notification response;
            switch (noti.getNotificationType()) {
                case UserRegistration:
                    System.out.println("User registered, id = " + noti.getClientID() + ", city = " + noti.getCity());
                    synchronized (observersByCity) {
                        //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                        //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                        NotiObserverClass usrObs = new NotiObserverClass(noti.getClientID(), null);
                        attachUserObserver(noti.getCity(), usrObs);
                    }
                    //notifica l'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserRegistration, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);
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
                        NotiObserverClass orgObs = new NotiObserverClass(noti.getClientID(), out);
                        attachOrgObserver(noti.getEventID(), orgObs);
                    }
                    //notifica l'organizer
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EventAdded, noti.getClientID(), null, null, null, null, null, null);
                    sendNotificationToClient(response, out);

                    //notifica l'utente
                    notifyUserObservers(noti.getCity(), noti.getEventID(), noti.getClientID(), noti.getNotificationType());
                    break;

                case EventDeleted:
                    System.out.println("Event with id " + noti.getEventID() + " deleted");
                    synchronized (organizersByEventID) {
                        //rimuove l'associazione tra event-id e organizer nella hashmap
                        organizersByEventID.remove(noti.getEventID());
                        detachOrgObserver(noti.getEventID());
                    }
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EventDeleted, null, null, noti.getEventID(), null, null, null, null);
                    sendNotificationToClient(response, out);

                    //notifica l'utente
                    notifyUserObservers(noti.getCity(), noti.getEventID(), noti.getClientID(), noti.getNotificationType());

                    break;

                case UserEventParticipation:
                    System.out.println("User with id " + noti.getClientID() + " participating to event with id = " + noti.getEventID());

                    //mandare messaggio di ritorno all'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserEventParticipation, null, null, null, null, null, null, null);
                    sendNotificationToClient(response, out);

                    //mandare notifica all'organizzatore
                    notifyOrgObserver(noti.getClientID(), noti.getEventID(), noti.getNotificationType());

                    break;

                case UserEventRemoval:
                    System.out.println("User with id " + noti.getClientID() + " removed participation to event with id = " + noti.getEventID());

                    //mandare messaggio di ritorno all'utente
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.UserEventRemoval, noti.getClientID(), null, noti.getEventID(), null, null,null, null);
                    sendNotificationToClient(response, out);
                    break;

                case ChangeCity:
                    System.out.println("User with id = " + noti.getClientID() + ", changed city from " + noti.getCity() + " to " +  noti.getNewCity());

                    synchronized (observersByCity) {
                        //DETACH CITY VECCHIA
                        detachUserObs(noti.getCity(), noti.getClientID());

                        //ATTACH SU CITY NUOVA
                        NotiObserverClass usrObs = new NotiObserverClass(noti.getClientID(), null);
                        attachUserObserver(noti.getNewCity(), usrObs);

                        //UPDATE CANALE DI OUTPUT
                        updateUserOut(noti.getNewCity(), noti.getClientID(), out);
                    }

                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.ChangeCity, noti.getClientID(), null, null, null, noti.getCity(), noti.getNewCity(), null);
                    sendNotificationToClient(response, out);
                    break;

                case GroupJoin:
                    System.out.println("User with id = " + noti.getClientID() + ", joined group with id = " + noti.getEventID()); //event viene usato come group

                    //AGGIORNO HASHMAP
                    synchronized (usersInGroups) {
                        MessageObserverClass groupObs = new MessageObserverClass(noti.getClientID(), out);
                        attachObsToGroup(noti.getEventID(), groupObs);
                    }

                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GroupJoin, null, null, noti.getEventID(), null, null, null, null);
                    sendNotificationToClient(response, out);
                    break;

                case GroupLeave:
                    response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GroupLeave, null, null, noti.getEventID(), null, null, null, null);

                    synchronized (usersInGroups) {
                        if (detachObsFromGroup(noti.getClientID(), noti.getEventID())) {
                            System.out.println("User with id = " + noti.getClientID() + ", left group with id = " + noti.getEventID());
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
                    clientRunning = false;
                    break;
            }
        }
    }

    private void notifyGroupUsers(Integer receiverID, Message mex) {
        List<MessageObserverClass> usersObsList = usersInGroups.get(receiverID);
        if (usersObsList != null) {
            for (MessageObserverClass obs : usersObsList){
                if (connectedUsers.get(obs.getObsID())) {
                    //se user online mandagli il messaggio per aggiungerlo alla chat
                    obs.update(mex);
                }
            }

        } else {
            logger.severe("Org id got from observer is null - can't update observers");
            throw new RuntimeException();
        }
    }

    private void loadData() {
        try {
            UserDAO userDAO;
            switch (PersistenceClass.getPersistenceType()){
                case FileSystem:
                    try {
                        userDAO = new UserDAOCSV();
                        logger.info("Server working on filesystem");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case JDBC:
                default:
                    userDAO = new UserDAOJDBC();
                    logger.info("Server working on database");
                    break;
            }

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
        } catch (InvalidClassException e){
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object noti");
        } catch (IOException e) {
            //gestione eccezioni IO
            logger.severe(e.getMessage());
        }
    }

    private void updateUserOut(String city, int clientID, ObjectOutputStream out) {
        //aggiorna canale di output nella hashmap delle citta'
        List<NotiObserverClass> users = observersByCity.get(city);
        for (NotiObserverClass user : users) {
            if (user.getObsID() == clientID) {
                user.setOut(out);
            }
        }
        synchronized (usersInGroups){
            //aggiorna canale di output nell'hashmap dei gruppi
            for (Map.Entry<Integer, List<MessageObserverClass>> entry : usersInGroups.entrySet()) {
                for (MessageObserverClass user : entry.getValue()) {   //entry.getValue() mi restituisce una lista di observers
                    if (user.getObsID() == clientID) {
                        user.setOut(out);
                    }
                }
            }
        }
    }

    //TODO: sistemare interfaccia Subject con attach, detach e notify

    private void updateOrgOut(int orgID, ObjectOutputStream out) {
        for (Map.Entry<Integer, NotiObserverClass> entry : organizersByEventID.entrySet()) {
            if (entry.getValue().getObsID() == orgID) {
                entry.getValue().setOut(out);
            }
        }
    }

    private void notifyUserObservers(String city, int eventID, int userID, NotificationTypes type) {
        List<NotiObserverClass> users = observersByCity.get(city);
        if (users != null) {
            for (NotiObserverClass user : users) {
                if (connectedUsers.get(user.getObsID())) {
                    //se utente online notifica
                    user.update(type);
                }
                //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
                notifyDAO.addNotification(user.getObsID(), type, eventID, userID);
            }
        }
    }

    private void notifyOrgObserver(int userID, int eventID, NotificationTypes type) {
        NotiObserverClass org = organizersByEventID.get(eventID);
        if (org != null) {
            if (connectedOrganizers.get(org.getObsID())) {
                //se org online notifica
                org.update(type);
            }
            //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
            notifyDAO.addNotification(org.getObsID(), type, eventID, userID);

        } else {
            logger.severe("Org id got from observer is null - can't update observers");
            throw new RuntimeException();
        }
    }


    private void attachOrgObserver(int eventID, NotiObserverClass orgObs) {
        organizersByEventID.put(eventID, orgObs);
        System.out.println("Added eventID: " + eventID + " to orgID: " + orgObs.getObsID());
    }

    private void attachUserObserver(String city, NotiObserverClass obs) {
        observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(obs);
        System.out.println("Added city: " + city + " to userID: " + obs.getObsID());
    }

    private void attachObsToGroup(Integer groupID, MessageObserverClass groupObs) {
        usersInGroups.computeIfAbsent(groupID, k -> new ArrayList<>()).add(groupObs);
        System.out.println("Added userID: " + groupObs.getObsID() + " to groupID: " + groupID);
    }

    private void detachOrgObserver(Integer eventID) {
        NotiObserverClass tempObs =  organizersByEventID.remove(eventID);
        System.out.println("Removed eventID: " + eventID + " to orgID: " + tempObs.getObsID());
    }

    private boolean detachUserObs(String city, Integer userID){
        List<NotiObserverClass> list = observersByCity.get(city);
        if (list!=null){
            list.removeIf(obs -> obs.getObsID() == userID);
            System.out.println("Detached user id " + userID + " from city "+city);
            return true;
        }
        return false;
    }
    private boolean detachObsFromGroup(Integer clientID, Integer groupID) {
        List<MessageObserverClass> tempList = usersInGroups.get(groupID);
        if (tempList != null) {
            tempList.removeIf(obs -> obs.getObsID() == clientID);
            System.out.println("Removed userID: " + clientID + " from groupID: " + groupID);
            return true;
        }
        return false;
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

}

