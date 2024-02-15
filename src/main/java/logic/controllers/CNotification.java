package logic.controllers;

import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.model.NotificationMessage;
import logic.server.Server;
import logic.utils.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class CNotification {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private static final SituationType NOTIFICATION = SituationType.Notification;
    private Semaphore semaphore;
    private Socket client;
    private ClientListener listener;
    private Thread listenerThread;
    private NotificationDAO notificationDAO;
    private MessageFactory msgFactory;
    private SecureObjectInputStream in;
    private ObjectOutputStream out;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
        this.msgFactory = new MessageFactory();
    }

    private void startListener(int userID){
        try {
            client = new Socket(Server.ADDRESS, Server.PORT);
            //setActive(true);
            this.semaphore = new Semaphore(1);

            //apriamo la socket e i canali in/out
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new SecureObjectInputStream(client.getInputStream());

            //avviamo il thread con il listener
            this.listener = new ClientListener(userID, this.semaphore, this, this.in);
            this.listenerThread = new Thread(listener);

            //setto il thread come demone affinché termini quando termina anche il thread principale
            this.listenerThread.setDaemon(true);
            //starto il thread
            this.listenerThread.start();

            logger.info("Client " + userID + " listener started successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(MessageTypes msgType, Integer clientID, Integer eventID, String city, UserTypes usrType){
        try {
            //se ListenerThread non è ancora stato inizializzato oppure è stato inizializzato ma è stato poi interrotto lo avvio
            if (listenerThread == null || !listenerThread.isAlive()){
                startListener(clientID);
            }
            //creo il messaggio e lo mando al server
            if (listenerThread.isAlive()){
                Message msg = msgFactory.createMessage(NOTIFICATION, msgType, clientID, eventID, city, usrType);
                out.writeObject(msg);
                out.flush();
                out.reset();
            }

            //aspetto sempre risposta del server
            semaphore.acquire(2);

            //Vedo il tipo di messaggio per decidere se chiudere il listener oppure no
            if (msgType == MessageTypes.UserRegistration || msgType==MessageTypes.Disconnected){
                stopListener(clientID);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopListener(int userID){
        //chiudo il thread listener del client
        try {
            listenerThread.interrupt();
            if (!client.isClosed()) {
                this.client.close();
                logger.info("Client " + userID + " socket closed successfully");
            }
        } catch (IOException e){
            logger.severe(e.getMessage());
        }
    }

    public ArrayList<Message> retrieveNotifications(int userID) {
        return this.notificationDAO.getNotificationsByUserID(userID);
    }
}
