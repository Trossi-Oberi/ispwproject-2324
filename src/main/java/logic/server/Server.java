package logic.server;

import logic.controllers.ObserverClass;
import logic.model.Message;
import logic.utils.MessageTypes;
import logic.utils.SecureObjectInputStream;
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

    private static Logger logger = Logger.getLogger("NightPlan");
    private static final int MAX_CONNECTIONS = 500;
    private ServerSocket serverSocket;


    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;



    public static void main(String[] args) {
        logger.info("Server running on port " + PORT);
        Server server = new Server();
        server.start();
    }

    private void start() {
        //TODO: implementare un preload dal db di tutti gli utenti ed eventi. Utenti associati alla città, eventi associati all'organizerID
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
                //imposto un timeout della socket a 5 secondi
                client.setSoTimeout(5000);
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
                    Message msg = (Message) in.readObject();
                    Message response;
                    switch (msg.getType()) {
                        case UserRegistration:
                            System.out.println("User registered, id = " + msg.getClientID() + ", city = " + msg.getCity());
                            synchronized (observersByCity) {
                                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                                ObserverClass usrObs = new ObserverClass(msg.getClientID(), null);
                                attachUserObserver(msg.getCity(), usrObs);
                            }
                            //notifica l'utente
                            response = new Message(MessageTypes.UserRegistration, msg.getClientID());
                            out.writeObject(response);
                            out.flush();
                            out.reset();

                            clientRunning = false;
                            break;
                        case LoggedIn:
                            System.out.println("User logged in, id = " + msg.getClientID());

                            synchronized (connectedUsers){
                                updateLoggedUsers(msg.getClientID(), true);
                            }
                            synchronized (observersByCity) {
                                if(msg.getUserType() == UserTypes.USER) {
                                    updateUserOut(msg.getCity(), msg.getClientID(), out);
                                } else {
                                    //TODO: fare updateOrgOut
                                }
                            }
                            //notifica l'utente
                            response = new Message(MessageTypes.LoggedIn, msg.getClientID());
                            out.writeObject(response);
                            out.flush();
                            out.reset();
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
                            response = new Message(MessageTypes.EventAdded, msg.getClientID());
                            out.writeObject(response);
                            out.flush();
                            out.reset();

                            //notifica l'utente
                            updateUserObservers(msg.getCity());
                            break;

                        case UserEventParticipation:
                            break;

                        case Disconnected:
                            System.out.println("User logged out, id = " + msg.getClientID());
                            synchronized (connectedUsers) {
                                updateLoggedUsers(msg.getClientID(), false);
                            }
                            //notifica l'utente
                            response = new Message(MessageTypes.Disconnected, msg.getClientID());
                            out.writeObject(response);
                            out.flush();
                            out.reset();

                            clientRunning = false;
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
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

    private void updateUserObservers(String city) {
        List<ObserverClass> users = observersByCity.get(city);
        for (ObserverClass user : users) {
            if (connectedUsers.get(user.getObsID())) {
                //se utente online notifica
                user.update(MessageTypes.EventAdded);
            }
            //TODO: aggiunta al database
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

    private void updateLoggedUsers(int userID, boolean isConnected){
        connectedUsers.put(userID, isConnected);
        System.out.println("User id: " + userID + ", isConnected:" + connectedUsers.get(userID).toString());
    }

}
