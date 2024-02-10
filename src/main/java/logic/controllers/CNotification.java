package logic.controllers;

import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.server.Server;
import logic.utils.MessageTypes;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class CNotification {
    private NotificationDAO notificationDAO;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket client;
    private ClientListener listener;
    private boolean active = false; //TODO: SETTARE TRUE

    public CNotification() {
        this.notificationDAO = new NotificationDAO();
        try {
            client = new Socket(Server.SERVER_ADDRESS, Server.PORT);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));

            System.out.printf("socket aperta (client)");
        } catch (IOException e) {
            //ignore
        }
    }

    public void connectToNotificationServer(int id) {
        setActive();
        Semaphore semaphore = new Semaphore(1);

        //COMMENTATO MOMENTANEAMENTE sendLoginMessage(id);

        listener = new ClientListener(id, this, semaphore);
        Thread t = new Thread(listener);
        t.start();

        try {
            semaphore.acquire(2);
        } catch (InterruptedException e) {
            System.out.println("Semaphore(2) not acquirable: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        /*if (!active) {
            throw new ServerDownException(username);
        }*/
    }


    public void sendRegMessage(int id, String city) {
        try {
            Message msg = createMessage(MessageTypes.UserRegistration, id, -1, city);
            out.writeObject(msg);
            out.reset();
        } catch (IOException e) {
            //ignore
        }
    }

    public Message createMessage(MessageTypes type, int userID, int eventID, String city) {
        Message msg = null;
        switch (type) {
            case UserRegistration:
                msg = new Message(type, userID, city);
                break;
            case LoggedIn:
                msg = new Message(type, userID, eventID, null); //prova temporanea
                break;
            case EventAdded:
                msg = new Message(type, userID, eventID, city);
                break;
            case Disconnected:
                msg = new Message(type, userID);
                break;
            case Start:
                msg = new Message(type);
                break;
        }
        return msg;
    }

    public void sendAddEventMessage(int orgID, int eventID, String eventCity) {
        try {
            //creazione messaggio
            Message message = createMessage(MessageTypes.EventAdded, orgID, eventID, eventCity);
            out.writeObject(message);
            out.reset();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginMessage(int clientID) {
        try {
            Message message = createMessage(MessageTypes.LoggedIn, clientID, -1, null);
            out.writeObject(message);
            out.reset();
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }

    public void setActive() {
        this.active = true;
    }

    public void setNotActive() {
        this.active = false;
    }
}



