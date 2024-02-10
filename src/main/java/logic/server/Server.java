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
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket client = serverSocket.accept();
                ClientHandler ch = new ClientHandler(client);
                Thread t = new Thread(ch);
                t.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket client;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                while (client.isConnected()) {
                    Message msg = (Message) in.readObject();
                    if (msg != null) {
                        switch (msg.getType()) {
                            case UserRegistration:
                                System.out.println("Utente registrato, id=" + msg.getClientID() + ", city=" + msg.getCity());
                                synchronized (observersByCity) {
                                    //creo observer
                                    ObserverClass usrObs = new ObserverClass(msg.getClientID(), out);
                                    attachUserObserver(msg.getCity(), usrObs);
                                }
                                //notifica l'utente
                                Message response = new Message(MessageTypes.UserRegistration);
                                out.writeObject(response);
                                break;
                        }
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                clientShutdown();
            }
        }

        private void clientShutdown(){
            try{
                out.close();
                in.close();
                client.close();
            }catch(IOException e){
                System.out.println("Exception in clientShutdown "+e.getMessage());
            }
        }


    }

    private void attachUserObserver(String city, ObserverClass obs) {
        observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(obs);
        System.out.println("Aggiunta corrispondenza: " + city + " - " + obs.getObsID());
    }

}

