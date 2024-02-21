package logic.server;

import logic.controllers.*;
import logic.dao.*;
import logic.interfaces.Subject;
import logic.model.CityData;
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


public class Server {
    private Map<String, List<ObserverClass>> observersByCity = new HashMap<>();
    private Map<Integer, ObserverClass> organizersByEventID = new HashMap<>();
    private Map<Integer, Boolean> connectedUsers = new HashMap<>();
    private Map<Integer, Boolean> connectedOrganizers = new HashMap<>();
    private Map<Integer, List<ObserverClass>> usersInGroups = new HashMap<>();

    private static Logger logger = Logger.getLogger("NightPlan");
    private int connections = 0;
    private static final int MAX_CONNECTIONS = 500;
    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;
    private NotificationFactory notiFactory;
    private ObserverFactory obsFactory;
    private static final SituationType SERVER_CLIENT = SituationType.SERVER_CLIENT;

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

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            //attivo la connessione del server tramite ServerSocket
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


    private class ClientHandler implements Runnable, Subject {
        private Socket client;
        private ObjectOutputStream out;

        private boolean clientRunning = true;

        private static final String USER_ID_STR = "User with id = ";

        public ClientHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            SecureObjectInputStream in = null;

            try {
                //quando viene avviato il thread associato al client parte questa funzione run()
                //apriamo i canali di input/output del client con il server.
                out = new ObjectOutputStream(client.getOutputStream()); //da server a client
                in = new SecureObjectInputStream(client.getInputStream()); //da client a server
            } catch (IOException e) {
                logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }

            try {
                while (clientRunning) { //questo ciclo while si interrompe non appena il Client chiude i suoi canali di comunicazione con il server
                    //blocco il thread in lettura di un messaggio in arrivo dal client
                    Object object = null;
                    if (in != null) {
                        object = in.readObject();
                    }
                    if (object instanceof Notification notification) {
                        handleNotification(notification);
                    } else if (object instanceof Message message) {
                        handleMessage(message);
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
            this.notify(SubjectTypes.USERS_IN_GROUP, mex);
            //notifico solo gli utenti online nel gruppo con quell'ID (receiverID)
        }

        private void handleNotification(Notification noti) {
            switch (noti.getNotificationType()) {
                case USER_REGISTRATION:
                    handleUserReg(noti);
                    break;

                case LOGGED_IN:
                    handleLogin(noti);
                    break;

                case EVENT_ADDED:
                    handleEventAdded(noti);
                    break;

                case EVENT_DELETED:
                    handleEventDeleted(noti);
                    break;

                case USER_EVENT_PARTICIPATION:
                    handleEventParticipation(noti);
                    break;

                case CHANGE_CITY:
                    handleChangeCity(noti);
                    break;

                case GROUP_JOIN:
                    handleGroupJoin(noti);
                    break;

                case GROUP_LEAVE:
                    handleGroupLeave(noti);
                    break;

                case DISCONNECTED:
                    handleDisconnected(noti);
                    break;
            }
        }

        private void handleDisconnected(Notification noti) {
            if (noti.getUserType().equals(UserTypes.USER)) {
                System.out.println("User logged out, id = " + noti.getClientID());
                synchronized (connectedUsers) {
                    updateLoggedUsers(noti.getClientID(), false, noti.getUserType());
                }
            } else if (noti.getUserType().equals(UserTypes.ORGANIZER)) {
                System.out.println("Organizer logged out, id = " + noti.getClientID());
                synchronized (connectedOrganizers) {
                    updateLoggedUsers(noti.getClientID(), false, noti.getUserType());
                }
            }

            //notifica l'utente
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.DISCONNECTED, noti.getClientID(), null, null, null, null);
            sendNotificationToClient(response, out);
            connections--;
            clientRunning = false;
        }

        private void handleGroupLeave(Notification noti) {
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GROUP_LEAVE, null, null, noti.getEventID(), null, null);

            synchronized (usersInGroups) {
                if (detach(SubjectTypes.USERS_IN_GROUP, noti)) {
                    System.out.println(USER_ID_STR + noti.getClientID() + ", left group with id = " + noti.getEventID());
                } else {
                    logger.severe("Error during GroupLeave detach");
                }
            }

            sendNotificationToClient(response, out);
        }

        private void handleGroupJoin(Notification noti) {
            System.out.println(USER_ID_STR + noti.getClientID() + ", joined group with id = " + noti.getEventID()); //event viene usato come group

            //AGGIORNO HASHMAP
            synchronized (usersInGroups) {
                if (!attach(SubjectTypes.USERS_IN_GROUP, noti, out)) {
                    logger.severe("Error during GroupJoin attach");
                }
            }

            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.GROUP_JOIN, null, null, noti.getEventID(), null, null);
            sendNotificationToClient(response, out);
        }

        private void handleChangeCity(Notification noti) {
            System.out.println(USER_ID_STR + noti.getClientID() + ", changed city from " + noti.getCity() + " to " + noti.getNewCity());

            synchronized (observersByCity) {
                //DETACH CITY VECCHIA
                if (detach(SubjectTypes.USERS_IN_CITY, noti)) {
                    System.out.println("User with id " + noti.getClientID() + " detached from city " + noti.getCity());
                } else {
                    logger.severe("Error during ChangeCity detach");
                }
                //ATTACH SU CITY NUOVA
                if (!attach(SubjectTypes.USERS_IN_CITY, noti, out)) {
                    logger.severe("Error during ChangeCity attach");
                }
            }

            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.CHANGE_CITY, noti.getClientID(), null, null, new CityData(noti.getCity(), noti.getNewCity()), null);
            sendNotificationToClient(response, out);
        }

        private void handleEventParticipation(Notification noti) {
            System.out.println("User with id " + noti.getClientID() + " participating to event with id = " + noti.getEventID());

            //mandare messaggio di ritorno all'utente
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.USER_EVENT_PARTICIPATION, null, null, null, null, null);
            sendNotificationToClient(response, out);

            //mandare notifica all'organizzatore
            if (!this.notify(SubjectTypes.EVENT_ORGANIZER, noti)) {
                logger.severe("Error during UserEventParticipation notify");
            }

        }

        private void handleEventDeleted(Notification noti) {
            System.out.println("Event with id " + noti.getEventID() + " deleted");
            synchronized (organizersByEventID) {
                //rimuove l'associazione tra event-id e organizer nella hashmap
                if (!detach(SubjectTypes.EVENT_ORGANIZER, noti)) {
                    logger.severe("Error during EventDeleted detach");
                }
            }
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EVENT_DELETED, null, null, noti.getEventID(), null, null);
            sendNotificationToClient(response, out);
        }

        private void handleEventAdded(Notification noti) {
            System.out.println("New event added by " + noti.getClientID() + ", eventID: " + noti.getEventID() + ", city: " + noti.getCity());
            synchronized (organizersByEventID) {
                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                if (!attach(SubjectTypes.EVENT_ORGANIZER, noti, out)) {
                    logger.severe("Error during EventAdded attach");
                }
            }
            //notifica l'organizer
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.EVENT_ADDED, noti.getClientID(), null, null, null, null);
            sendNotificationToClient(response, out);

            //notifica l'utente
            if (!this.notify(SubjectTypes.USERS_IN_CITY, noti)) {
                logger.severe("Error during EventAdded notify");
            }
        }

        private void handleLogin(Notification noti) {
            if (noti.getUserType().equals(UserTypes.USER)) {
                System.out.println("User logged in, id = " + noti.getClientID());
                synchronized (connectedUsers) {
                    updateLoggedUsers(noti.getClientID(), true, noti.getUserType());
                }
                synchronized (observersByCity) {
                    updateUserOut(noti.getCity(), noti.getClientID(), out);
                }
            } else if (noti.getUserType().equals(UserTypes.ORGANIZER)) {
                System.out.println("Organizer logged in, id = " + noti.getClientID());
                synchronized (connectedOrganizers) {
                    updateLoggedUsers(noti.getClientID(), true, noti.getUserType());
                }
                synchronized (organizersByEventID) {
                    updateOrgOut(noti.getClientID(), out);
                }
            }

            //notifica l'utente
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.LOGGED_IN, noti.getClientID(), null, null, null, null);
            sendNotificationToClient(response, out);
        }

        private void handleUserReg(Notification noti) {
            System.out.println("User registered, id = " + noti.getClientID() + ", city = " + noti.getCity());
            synchronized (observersByCity) {
                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                if (!attach(SubjectTypes.USERS_IN_CITY, noti, null)) {
                    logger.severe("Error during UserRegistration attach");
                }
            }
            //notifica l'utente
            Notification response = notiFactory.createNotification(SERVER_CLIENT, NotificationTypes.USER_REGISTRATION, noti.getClientID(), null, null, null, null);
            sendNotificationToClient(response, out);
            connections--;
            clientRunning = false;
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

        private void updateOrgOut(int orgID, ObjectOutputStream out) {
            for (Map.Entry<Integer, ObserverClass> entry : organizersByEventID.entrySet()) {
                if (entry.getValue().getObsID() == orgID) {
                    entry.getValue().setOut(out);
                }
            }
        }

        private void updateLoggedUsers(int userID, boolean isConnected, UserTypes type) {
            if (type.equals(UserTypes.USER)) {
                connectedUsers.put(userID, isConnected);
                System.out.println("User id: " + userID + ", isConnected:" + connectedUsers.get(userID).toString());
            } else if (type.equals(UserTypes.ORGANIZER)) {
                connectedOrganizers.put(userID, isConnected);
                System.out.println("Organizer id: " + userID + ", isConnected:" + connectedOrganizers.get(userID).toString());
            }
        }

        @Override
        public boolean attach(SubjectTypes type, Notification noti, ObjectOutputStream out) {
            switch (type) {
                case USERS_IN_CITY:
                    return attachUserToCity(noti, out);
                case EVENT_ORGANIZER:
                    return attachOrgToEvent(noti, out);
                case USERS_IN_GROUP:
                    return attachUserToGroup(noti, out);
            }
            return false;
        }

        @Override
        public boolean detach(SubjectTypes type, Notification noti) {
            switch (type) {
                case USERS_IN_CITY:
                    return detachUserFromCity(noti);
                case EVENT_ORGANIZER:
                    return detachOrgFromEvent(noti);
                case USERS_IN_GROUP:
                    return detachUserFromGroup(noti);
            }
            return false;
        }

        @Override
        public boolean notify(SubjectTypes type, Object o) {
            switch (type) {
                case USERS_IN_CITY:
                    return notifyUsersInCity((Notification) o);
                case EVENT_ORGANIZER:
                    return notifyEventOrg((Notification) o);
                case USERS_IN_GROUP:
                    return notifyUsersInGroup((Message) o);
            }
            return false;
        }

        //ATTACH
        private boolean attachOrgToEvent(Notification noti, ObjectOutputStream out) {
            if (noti == null) {
                return false;
            }
            ObserverClass orgObs = obsFactory.createObserver(ObserverType.NOTI_OBSERVER, noti.getClientID(), out);
            organizersByEventID.put(noti.getEventID(), orgObs);
            System.out.println("Added eventID: " + noti.getEventID() + " to orgID: " + orgObs.getObsID());
            return true;
        }

        private boolean attachUserToCity(Notification noti, ObjectOutputStream out) {
            if (noti == null) {
                return false;
            }
            ObserverClass notiObs = obsFactory.createObserver(ObserverType.NOTI_OBSERVER, noti.getClientID(), out);
            if(noti.getNewCity() != null){
                observersByCity.computeIfAbsent(noti.getNewCity(), k -> new ArrayList<>()).add(notiObs);
                System.out.println("Added userID " + notiObs.getObsID() + " to new city: " + noti.getNewCity());
            } else {
                observersByCity.computeIfAbsent(noti.getCity(), k -> new ArrayList<>()).add(notiObs);
                System.out.println("Added userID " + notiObs.getObsID() + " to city: " + noti.getCity());
            }

            return true;
        }

        private boolean attachUserToGroup(Notification noti, ObjectOutputStream out) {
            if (noti == null) {
                return false;
            }
            Integer groupID = noti.getEventID();
            ObserverClass groupObs = obsFactory.createObserver(ObserverType.MESSAGE_OBSERVER, noti.getClientID(), out);
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
            if (noti == null) {
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
                    Boolean res = connectedUsers.get(user.getObsID());
                    if (res != null && res) {
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
                return false;
            }
            Boolean res = connectedOrganizers.get(org.getObsID());
            if (res != null && res) {
                //se org online notifica
                org.update(noti);
            }
            return true;
        }

        private boolean notifyUsersInGroup(Message mex) {
            List<ObserverClass> usersObsList = usersInGroups.get(mex.getReceiverID());
            if (usersObsList != null) {
                for (ObserverClass obs : usersObsList) {
                    Boolean res = connectedUsers.get(obs.getObsID());
                    if (res != null && res) {
                        //se user online mandagli il messaggio per aggiungerlo alla chat
                        obs.update(mex);
                    }
                }

            } else {
                logger.severe("Org id got from observer is null - can't update observers");
                return false;
            }
            return true;
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


}

