package logic.controllers;

import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.server.Server;
import logic.utils.MessageTypes;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CNotification {
    private NotificationDAO notificationDAO;
    private ObjectOutputStream out;
    private Socket client;
    private ClientListenerThread t;

    public CNotification() {
        this.notificationDAO = new NotificationDAO();
        try {
            client = new Socket(Server.SERVER_ADDRESS, Server.PORT);
            System.out.printf("socket aperta (client)");
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            //ignore
        }
    }

    public void connectToNotificationServer(int id) {
        sendLoginMessage(id);
        NotiHandler notiHandler = new NotiHandler();
        t = new ClientListenerThread(client, notiHandler);
        //con l'inizializzazione del thread apro l'object input stream
        t.start();
        //ora lo starto e parte il metodo run
    }

    public void disconnectFromServer(int id) {
        try {
            Message msg = createMessage(MessageTypes.Disconnected, id, -1, null);
            out.writeObject(msg);
            if (t != null && t.isAlive()) {
                t.stopRunning();
            }
            out.close();
            if (!client.isClosed()) {
                //TODO: qui dentro non ci entra mai!!!!! anche se apparentemente non ho mai chiuso la socket
                client.close();
                System.out.printf("socket chiusa (client)");
            }
        } catch (IOException e) {
            //ignore
        }
    }

    public class NotiHandler {
        public void handleMessage(Message msg) {
            switch (msg.getType()) {
                case EventAdded:
                    EssentialGUI.showNotification();
                    break;
            }
        }
    }

    public void sendRegMessage(int id, String city) {
        try {
            Message msg = createMessage(MessageTypes.UserRegistration, id, -1, city);
            out.writeObject(msg);
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
        }
        return msg;
    }

    public void sendAddEventMessage(int orgID, int eventID, String eventCity){
        try {
            //creazione messaggio
            Message message = createMessage(MessageTypes.EventAdded, orgID, eventID, eventCity);
            out.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginMessage(int clientID){
        try {
            Message message = createMessage(MessageTypes.LoggedIn, clientID, -1, null);
            out.writeObject(message);
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }

}
