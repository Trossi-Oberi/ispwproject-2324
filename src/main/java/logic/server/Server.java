package logic.server;

import logic.controllers.ObserverClass;
import logic.model.Message;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private Map<String, List<ObserverClass>> observersByCity = new HashMap<>();
    private Map<Integer, ObserverClass> organizersByEventID = new HashMap<>();

    private static Logger logger = Logger.getLogger("NightPlan");

    public static final String ADDRESS = "localhost";
    public static final int PORT = 2521;
    private ServerSocket serverSocket;


    public static void main(String[] args) {
        logger.info("Server running on port " + PORT);
        Server server = new Server();
        server.start();
    }

    private void start() {
        try {
            //attivo la connessione del server tramite ServerSocket
            serverSocket = new ServerSocket(PORT);
            while (true) {
                //aspetto che un utente si connetta da client al server aprendo una Socket
                Socket client = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + client.getInetAddress() + " on port " + client.getPort());
                //imposto un timeout della socket a 5 secondi
                //client.setSoTimeout(5000);
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
        private ObjectInputStream in;
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
                in = new ObjectInputStream(client.getInputStream()); //da client a server
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                while (clientRunning) { //questo ciclo while si interrompe non appena il Client chiude i suoi canali di comunicazione con il server
                    //blocco il thread in lettura di un messaggio in arrivo dal client
                    Message msg = (Message) in.readObject();
                    switch (msg.getType()) {
                        case UserRegistration:
                            System.out.println("User registered, id = " + msg.getClientID() + ", city = " + msg.getCity());
                            synchronized (observersByCity) {
                                //un solo thread alla volta può read/write su observersByCity affinché i dati siano consistenti
                                //creo observer con le informazioni del nuovo utente registrato (user_id, canali di comunicazione in uscita verso la Client socket)
                                ObserverClass usrObs = new ObserverClass(msg.getClientID(), out);
                                attachUserObserver(msg.getCity(), usrObs);
                            }
                            //notifica l'utente
                            Message response = new Message(MessageTypes.UserRegistration, msg.getClientID());
                            out.writeObject(response);
                            out.flush();
                            out.reset();

                            clientRunning = false;

                            //clientShutdown();
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, e.getMessage());
            } finally {
                //quando il client ha chiuso la connessione con il server esso può chiudere le sue connessioni verso il client

            }
        }

        private void clientShutdown(){
            try{
                //chiudo canali in/out e poi la socket
                out.close();
                in.close();
                client.close();
            }catch(IOException e){
                logger.log(Level.SEVERE, "IOException during clientShutdown: " + e.getMessage());
            }
        }


    }

    private void attachUserObserver(String city, ObserverClass obs) {
        observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(obs);
        System.out.println("Aggiunta corrispondenza: " + city + " - " + obs.getObsID());
    }

}

