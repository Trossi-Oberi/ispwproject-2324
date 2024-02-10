package logic.controllers;

import logic.model.Message;
import logic.server.Server;
import logic.utils.MessageTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ClientListener implements Runnable {
    //chiamato dal NotificationController

    private Socket client;
    private Semaphore semaphore;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //protected Logger logger = Logger.getLogger("WIG");
    private CNotification cNotification;
    //private int myConnection = 0;
    private boolean running = true;
    private int clientID;


    public ClientListener(int id, CNotification cNotification, Semaphore semaphore) {
        clientID = id;
        this.cNotification = cNotification;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            client = new Socket(Server.SERVER_ADDRESS, Server.PORT);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Could not Connect");
            cNotification.setNotActive();
            running = true;
            semaphore.release(2);
        } catch (InterruptedException e1) {
            System.out.println("Semaphore not acquirable: " + e1.getMessage());
            Thread.currentThread().interrupt();
            semaphore.release(2);
        }

        if (running) {
            semaphore.release(2);
            System.out.println("Connection accepted " + client.getInetAddress() + ":" + client.getPort());
            try {
                connect();
                System.out.println("Sockets in and out ready!");
                while (client.isConnected()) {
                    Message message = null;
                    message = (Message) in.readObject();
                    if (message != null) {
                        handleMessage(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection closed!");
            }
        } else {
            Thread.currentThread().interrupt();
        }
    }

    public void connect(){
        Message msg = cNotification.createMessage(MessageTypes.Start, -1, -1, null);
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Can't write START message to server, "+e.getMessage());
        }
    }

    public void disconnect(int id){
        try {
            /*Message msg = createMessage(MessageTypes.Disconnected, id, -1, null);
            out.writeObject(msg);*/
            if (t != null && t.isAlive()) {
                nh.stopRunning();
            }
            out.close();
            in.close();
            if (!client.isClosed()) {
                //TODO: qui dentro non ci entra mai!!!!! anche se apparentemente non ho mai chiuso la socket
                client.close();
                System.out.printf("socket chiusa (client)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.getType()) {
            case EventAdded:
                EssentialGUI.showNotification();
                break;
        }
    }

}

