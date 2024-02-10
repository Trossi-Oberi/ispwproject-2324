package logic.server;

import logic.controllers.ObserverClass;
import logic.model.Message;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    //In architettura MVC il server si identifica come controller

    //ATTRIBUTI NECESSARI (BASE)
    public static final String SERVER_ADDRESS = "localhost";
    public static final int PORT = 2521;
    private ServerSocket server;
    private Socket client;
    //private ArrayList<ClientHandler> connectedClients;
    Map<Integer, Boolean> connClientMap; //mappa intero (id utente) e booleano (se e' connesso)
    private boolean serverRunning;
    //private ExecutorService pool; //thread pool per non creare sempre nuovi threads
    private int currConnections = 0;
    private static final int MAX_CONNECTIONS = 500;
    private Thread x;

    //ATTRIBUTI SPECIFICI PER LE IMPLEMENTAZIONI DI OBSERVER ECC
    protected static Logger logger = Logger.getLogger("NightPlan");
    private Map<String, List<ObserverClass>> observersByCity; //mapping citta' e tutti gli utenti di quella citta'
    private Map<Integer, ObserverClass> organizerByEventID; //mapping tra eventID e relativo organizer (sto imponendo unico organizzatore per l'evento)



    public Server() {
        //connectedClients = new ArrayList<>();
        connClientMap = new HashMap<>();
        serverRunning = true;
        this.observersByCity = new HashMap<>();
        this.organizerByEventID = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);
            //pool = Executors.newCachedThreadPool();
            System.out.println("Server avviato sulla porta " + PORT);
            while(serverRunning){
                client = server.accept();
                System.out.println("Nuovo client connesso " + client);
                ClientHandler handler = new ClientHandler(client);
                //connectedClients.add(handler);

                //pool.execute(handler); //un thread della thread pool esegue il clienthandler
                x = new Thread(handler);
                x.start();
                currConnections++;
                if (currConnections == MAX_CONNECTIONS) {
                    break;
                }
                Thread.sleep(5000);

            }

        } catch (IOException | InterruptedException e) {
            //todo: handle
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public class ClientHandler implements Runnable {
        private Socket client;
        private ObjectInputStream in; //canale di input (legge dal client)
        private ObjectOutputStream out; //canale di output (scrive verso il client)
        private Message receivedMsg; //messaggio che riceviamo dal client
        private Message sentMsg; //messaggio che inviamo al client
        private String name;
        private Logger logger = Logger.getLogger("NightPlan");

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            System.out.println("thread avviato");
            try {
                //Apro gli stream di input e output
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());


                //PROVA
                Message startMsg = new Message(MessageTypes.Start);
                out.writeObject(startMsg);
                out.reset();

                //Aperti questi canali mi metto in attesa di messaggi da parte del client
                while ((!client.isClosed() && (receivedMsg = (Message) in.readObject())!= null)) {
                    connClientMap.put(receivedMsg.getClientID(),true); //se ricevo un messaggio dall'utente inizialmente do per scontato che l'utente e' connesso
                    //Gestione del tipo di messaggio
                    switch (receivedMsg.getType()) {
                        case UserRegistration:
                            System.out.println("User registration: user id=" + receivedMsg.getClientID() + ", citta'=" + receivedMsg.getCity());
                            //implementare factory observer e factory messaggi
                            ObserverClass userObs = new ObserverClass(receivedMsg.getClientID(), out);
                            attachUserObserver(receivedMsg.getCity(), userObs);

                            //CODICE DI TEST
                            Message sndmsg = new Message(MessageTypes.UserRegistration);
                            out.writeObject(sndmsg);
                            out.reset();
                            //FINE CODICE TEST

                            break;

                        /*case LoggedIn:
                            System.out.println("User login: user id=" + receivedMsg.getClientID());
                            break;

                        case EventAdded:
                            //EventObserver istanziazione e attach all'evento
                            System.out.println("Event added: org id=" + receivedMsg.getClientID() + ", event id=" + receivedMsg.getEventID());
                            ObserverClass orgObs = new ObserverClass(receivedMsg.getClientID(), out);
                            attachOrganizerObserver(receivedMsg.getEventID(), orgObs);
                            notifyUserObservers(receivedMsg.getCity());
                            break;

                        case UserEventParticipation:
                            break;

                        case Disconnected:
                            System.out.println("Client with id "+receivedMsg.getClientID()+ " disconnected");
                            connClientMap.put(receivedMsg.getClientID(),false);
                            //clientShutdown();
                            break;*/
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                //todo: handle
            }finally{
                clientShutdown();
            }
        }

        private void clientShutdown(){
            if(!client.isClosed()){
                try {
                    out.close();
                    in.close();
                    client.close();
                    System.out.println("Client socket disconnected from server");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        private void attachUserObserver(String city, ObserverClass userObs) {
            observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(userObs);
            System.out.println("aggiunta corrispondenza "+city+" - "+userObs.getObsID());
        }

        private void attachOrganizerObserver(int eventID, ObserverClass orgObs) {
            organizerByEventID.put(eventID, orgObs);
        }

        private void notifyUserObservers(String city) {
            List<ObserverClass> obsList = observersByCity.get(city);
            if (obsList != null) {
                for (ObserverClass obs : obsList) {
                    if(observerIsOnline(obs)){  //se l'utente (id) corrispondente all'observer e' segnato come online nella hashmap allora aggiornalo inviando la notifica popup
                        obs.update(MessageTypes.EventAdded);
                    }else{
                        //l'utente e' al momento offline -> aggiornamento database notifiche senza notifica popup
                    }
                }
            }
        }

        private boolean observerIsOnline(ObserverClass obs){
            return connClientMap.get(obs.getObsID());
        }
    }
    public static void main(String[] args) {
        logger.info("The notification server is running.");
        Server server = new Server();
        server.run();
    }
}
