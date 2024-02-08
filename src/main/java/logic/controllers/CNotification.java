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

public class CNotification {

    private Socket socket;
    private ObjectInputStream objInputStream;
    private ObjectOutputStream objOutputStream;
    private boolean running;
    private NotificationDAO notificationDAO;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
    }
    public void connectToNotificationServer(int clientID){
        try {
            // Crea una socket per la connessione al server
            this.socket = new Socket(NotificationServer.SERVER_ADDRESS, NotificationServer.PORT);

            // Ottiene il flusso di output della socket
            objOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Message message;

            //creazione messaggio
            if (LoggedUser.getUserType() == UserTypes.USER){
                message = new Message(MessageTypes.UserLogin, clientID);
            }else{
                message = new Message(MessageTypes.OrganizerLogin, clientID);
            }

            objOutputStream.writeObject(message);
            objOutputStream.close();
            running = true;
            Thread notificationReceiverThread = new Thread(this::receiveNotifications);
            notificationReceiverThread.start();


            //socket non va chiusa
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveNotifications(){
        try {
            while (running) {
                objInputStream = new ObjectInputStream(socket.getInputStream());
                Message receivedMessage = (Message) objInputStream.readObject();
                switch (receivedMessage.getType()){
                    case EventAdded:
                        EssentialGUI.showNotification();
                        break;
                }
            }
            // Chiudi il reader e il socket quando il thread termina
            objInputStream.close();
            this.socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
