package logic.controllers;

import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.server.NotificationServer;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class CNotification {

    private boolean running;
    private NotificationDAO notificationDAO;

    public CNotification() {
        this.notificationDAO = new NotificationDAO();
    }

    public void connectToNotificationServer(int clientID) {
        Thread notificationReceiverThread = new Thread(()-> receiveNotifications(clientID));
        notificationReceiverThread.start();
    }

    public void receiveNotifications(int clientID) {
        Socket socket;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        running = true;

        try {
            // Crea una socket per la connessione al server
            socket = new Socket(NotificationServer.SERVER_ADDRESS, NotificationServer.PORT);

            // Ottiene il flusso di output della socket
            oos = new ObjectOutputStream(socket.getOutputStream());
            Message message;

            //creazione messaggio
            if (LoggedUser.getUserType() == UserTypes.USER) {
                message = new Message(MessageTypes.UserLogin, clientID);
            } else {
                message = new Message(MessageTypes.OrganizerLogin, clientID);
            }

            oos.writeObject(message);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());

            while (running) {
//                ObjectInputStream objInputStream = new ObjectInputStream(socket.getInputStream());
                Message receivedMessage = (Message) ois.readObject();
                switch (receivedMessage.getType()) {
                    case EventAdded:
                        EssentialGUI.showNotification();
                        break;
                }
            }
            // Chiudi il reader e il socket quando il thread termina
            ois.close();
            oos.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
