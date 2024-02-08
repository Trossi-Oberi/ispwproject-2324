package logic.server;

import logic.controllers.ObserverClass;
import logic.model.Message;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationServer {
    //In architettura MVC il server si identifica come controller
    public static final String SERVER_ADDRESS = "localhost";
    public static final int PORT = 2521;
    protected static Logger logger = Logger.getLogger("NightPlan");
    private int currConnections = 0;
    private Map<String, List<ObserverClass>> observersByCity; //mapping citta' e tutti gli utenti di quella citta'
    private Map<Integer, ObserverClass> organizerByEventID; //mapping tra eventID e relativo organizer (sto imponendo unico organizzatore per l'evento)
    private static final int MAX_CONNECTIONS = 500;

    public static void main(String[] args) {
        logger.info("The notification server is running.");
        NotificationServer notificationServer = new NotificationServer();
        notificationServer.startServer();
    }

    public NotificationServer(){
        this.observersByCity = new HashMap<>();
        this.organizerByEventID = new HashMap<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server avviato sulla porta " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso "+ clientSocket);
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
                currConnections++;
                if (currConnections == MAX_CONNECTIONS) {
                    break;
                }
            }
        }catch (Exception e) {
            //logger.log(Level.SEVERE, EXCEPTION);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private String name;
        private Socket clientSocket;
        private Logger logger = Logger.getLogger("NightPlan");
        //private UserChatModel user;
        private String usersGroup;

        public ClientHandler(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            logger.info("Attempting to connect a user...");
        }

        public void start(){
            try {
                // Ottiene il flusso di input della socket
                ObjectInputStream objInputStream = new ObjectInputStream(clientSocket.getInputStream());
                Message message = (Message) objInputStream.readObject();

                // Identifica il tipo di messaggio e agisce di conseguenza
                switch(message.getType()){
                    case UserRegistration:
                        ObserverClass userObs = new ObserverClass(message.getClientID());
                        attachUserObserver(message.getCity(),userObs);
                        break;

                    case EventAdded:
                        //EventObserver istanziazione e attach all'evento
                        ObserverClass orgObs = new ObserverClass(message.getClientID());
                        attachOrganizerObserver(message.getEventID(), orgObs);
                        notifyUserObservers(message.getCity());
                        break;

                    case UserEventParticipation:
                        break;

                }

                // Chiude la socket dopo aver gestito il messaggio
                //clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void attachUserObserver(String city, ObserverClass userObs) {
            observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(userObs);
        }
        private void attachOrganizerObserver(int eventID, ObserverClass orgObs){
            organizerByEventID.put(eventID,orgObs);
        }

        private void notifyUserObservers(String city){
            List<ObserverClass> obsList = observersByCity.get(city);
            if (obsList!=null){
                for(ObserverClass obs : obsList){
                    obs.update(MessageTypes.EventAdded, clientSocket);
                }
            }

        }
    }
}
