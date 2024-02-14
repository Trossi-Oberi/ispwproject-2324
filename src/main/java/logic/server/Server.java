package logic.server;

import logic.controllers.MessageFactory;
import logic.controllers.ObserverClass;
import logic.dao.EventDAO;
import logic.dao.NotificationDAO;
import logic.dao.UserDAO;
import logic.model.Message;
import logic.model.NotificationMessage;
import logic.utils.MessageTypes;
import logic.utils.SecureObjectInputStream;
import logic.utils.SituationType;
import logic.utils.UserTypes;

import java.io.IOException;
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

    private static Logger logger = Logger.getLogger("NightPlan");
    private static final int MAX_CONNECTIONS = 500;
    private ServerSocket serverSocket;


    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;
    private MessageFactory msgFactory;
    private static final SituationType NOTIFICATION = SituationType.Notification;
    private static final SituationType GROUPCHAT = SituationType.GroupChat;

    public Server(){
        msgFactory = new MessageFactory();
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

        int connections = 0;
        boolean serverRunning = true;

        try {
            //attivo la connessione del server tramite ServerSocket
            serverSocket = new ServerSocket(PORT);
            while (serverRunning) {
                //verifico che non si sia raggiunto il numero massimo di connessioni possibili al server
                if(connections == MAX_CONNECTIONS){
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
        public void run(){
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
                    NotificationMessage msg = (NotificationMessage) in.readObject();
                    Message response;
                    switch (msg.getMessageType()) {
                        case UserRegistration:
                            System.out.println("User registered, id = " + msg.getClientID() + ", city = " + msg.getCity());
                            synchronized (observersByCity) {
                                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                                ObserverClass usrObs = new ObserverClass(msg.getClientID(), null);
                                attachUserObserver(msg.getCity(), usrObs);
                            }
                            //notifica l'utente
                            response = msgFactory.createMessage(NOTIFICATION, MessageTypes.UserRegistration, msg.getClientID(), null, null, null);
                            sendMessageToClient(response, out);
                            clientRunning = false;
                            break;

                        case LoggedIn:
                            switch (msg.getUserType()){
                                case USER:
                                    System.out.println("User logged in, id = " + msg.getClientID());
                                    synchronized (connectedUsers){
                                        updateLoggedUsers(msg.getClientID(), true, msg.getUserType());
                                    }
                                    synchronized (observersByCity) {
                                        updateUserOut(msg.getCity(), msg.getClientID(), out);
                                    }
                                    break;

                                case ORGANIZER:
                                    System.out.println("Organizer logged in, id = " + msg.getClientID());
                                    synchronized (connectedOrganizers){
                                        updateLoggedUsers(msg.getClientID(), true, msg.getUserType());
                                    }
                                    synchronized (organizersByEventID) {
                                        updateOrgOut(msg.getClientID(), out);
                                    }
                                    break;
                            }

                            //notifica l'utente
                            response = msgFactory.createMessage(NOTIFICATION, MessageTypes.LoggedIn, msg.getClientID(), null, null, null);
                            sendMessageToClient(response, out);
                            break;

                        case EventAdded:
                            System.out.println("New event added by " + msg.getClientID() + ", eventID: " + msg.getEventID() + ", city: " + msg.getCity());
                            synchronized (organizersByEventID) {
                                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                                ObserverClass orgObs = new ObserverClass(msg.getClientID(), out);
                                attachOrgObserver(msg.getEventID(), orgObs);
                            }
                            //TODO: fare stesso controllo del canale out anche per l'organizer, se l'organizer fa logout dopo aver aggiunto l'evento è un problema
                            //notifica l'organizer
                            response = msgFactory.createMessage(NOTIFICATION, MessageTypes.EventAdded, msg.getClientID(), null, null, null);
                            sendMessageToClient(response, out);

                            //notifica l'utente
                            updateUserObservers(msg.getCity(), msg.getEventID());
                            break;

                        case EventDeleted:
                            System.out.println("Event with id "+msg.getEventID()+" deleted");
                            synchronized (organizersByEventID){
                                //rimuove l'associazione tra event-id e organizer nella hashmap
                                organizersByEventID.remove(msg.getEventID());
                            }
                            response = msgFactory.createMessage(NOTIFICATION, MessageTypes.EventDeleted, null, msg.getEventID(), null, null);
                            sendMessageToClient(response, out);

                        case UserEventParticipation:
                            //TODO: usereventparticipation logic in server
                            break;

                        case Disconnected:
                            switch (msg.getUserType()){
                                case USER:
                                    System.out.println("User logged out, id = " + msg.getClientID());
                                    synchronized (connectedUsers) {
                                        updateLoggedUsers(msg.getClientID(), false, msg.getUserType());
                                    }

                                    break;
                                case ORGANIZER:
                                    System.out.println("Organizer logged out, id = " + msg.getClientID());
                                    synchronized (connectedOrganizers) {
                                        updateLoggedUsers(msg.getClientID(), false, msg.getUserType());
                                    }
                                    break;
                            }

                            //notifica l'utente
                            response = msgFactory.createMessage(NOTIFICATION, MessageTypes.Disconnected, msg.getClientID(), null, null, null);
                            sendMessageToClient(response, out);
                            clientRunning = false;
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            UserDAO userDAO = new UserDAO();
            EventDAO eventDAO = new EventDAO();
            userDAO.populateObsByCity(this.observersByCity);
            eventDAO.populateOrgByEventID(this.organizersByEventID);
        } finally {
            logger.info("Finished to preload data from database");
        }
    }

    private void sendMessageToClient(Message msg, ObjectOutputStream out){
        try {
            out.writeObject(msg);
            out.flush();
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserOut(String city, int clientID, ObjectOutputStream out) {
        List<ObserverClass> users = observersByCity.get(city);
        for (ObserverClass user : users) {
            if (user.getObsID() == clientID) {
                user.setOut(out);
            }
        }
    }

    private void updateOrgOut(int orgID, ObjectOutputStream out) {
        for (Map.Entry<Integer, ObserverClass> entry : organizersByEventID.entrySet()) {
            if(entry.getValue().getObsID() == orgID){
                entry.getValue().setOut(out);
            }
        }
    }

    private void updateUserObservers(String city, int eventID) {
        List<ObserverClass> users = observersByCity.get(city);
        NotificationDAO notifyDAO = new NotificationDAO();
        if(users != null) {
            for (ObserverClass user : users) {
                if (connectedUsers.get(user.getObsID())) {
                    //se utente online notifica
                    user.update(MessageTypes.EventAdded);
                }
                //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
                notifyDAO.addNotificationsToUser(user.getObsID(), MessageTypes.EventAdded, eventID);
            }
        }
    }

    private void attachOrgObserver(int eventID, ObserverClass orgObs) {
        organizersByEventID.put(eventID, orgObs);
        System.out.println("Added eventID: " + eventID + " to orgID: " + orgObs.getObsID());
    }

    private void attachUserObserver(String city, ObserverClass obs) {
        observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(obs);
        System.out.println("Added city: " + city + " to userID: " + obs.getObsID());
    }

    private void updateLoggedUsers(int userID, boolean isConnected, UserTypes type){
        switch (type){
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

