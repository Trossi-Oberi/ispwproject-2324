package logic.controllers;

import logic.beans.BNotification;
import logic.dao.NotificationDAO;
import logic.model.Notification;
import logic.server.Server;
import logic.utils.*;
import logic.view.NotificationView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class CNotification {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private static final SituationType NOTIFICATION = SituationType.ServerClient;
    private Semaphore semaphore;
    private Socket client;
    private ClientListener listener;
    private Thread listenerThread;
    private NotificationDAO notificationDAO;
    private NotificationFactory notiFactory;
    private SecureObjectInputStream in;
    private ObjectOutputStream out;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
        this.notiFactory = new NotificationFactory();
    }

    private void startListener(int userID, NotificationView notiView){
        try {
            client = new Socket(Server.ADDRESS, Server.PORT);
            //setActive(true);
            this.semaphore = new Semaphore(1);

            //apriamo la socket e i canali in/out
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new SecureObjectInputStream(client.getInputStream());

            //avviamo il thread con il listener
            this.listener = new ClientListener(notiView, userID, this.semaphore, this, this.in);
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

    public void sendNotification(NotificationView notiView, NotificationTypes notiType, Integer clientID, Integer notifierID, Integer eventID, Integer notificationID, String city, UserTypes usrType){
        try {
            //se ListenerThread non è ancora stato inizializzato oppure è stato inizializzato ma è stato poi interrotto lo avvio
            if (listenerThread == null || listenerThread.isInterrupted()){
                startListener(clientID, notiView);
            }
            //creo il messaggio e lo mando al server
            if (listenerThread.isAlive()){
                Notification noti = notiFactory.createNotification(NOTIFICATION, notiType, clientID, notifierID, eventID, notificationID, city, usrType);
                out.writeObject(noti);
                out.flush();
                out.reset();
            }

            //aspetto sempre risposta del server
            semaphore.acquire(2);

            //Vedo il tipo di messaggio per decidere se chiudere il listener oppure no
            if (notiType == NotificationTypes.UserRegistration || notiType== NotificationTypes.Disconnected){
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

    public ArrayList<BNotification> retrieveNotifications(int userID) {
        ArrayList<Notification> notifications = new ArrayList<>(this.notificationDAO.getNotificationsByUserID(userID));
        return makeBeanFromModel(notifications);
    }

    private ArrayList<BNotification> makeBeanFromModel(ArrayList<Notification> notifications){
        BNotification notiBean;
        ArrayList <BNotification> notiBeanList = new ArrayList<>();
        for (Notification noti : notifications){
            notiBean = new BNotification();
            notiBean.setMessageType(noti.getNotificationType());
            notiBean.setClientID(noti.getClientID());
            notiBean.setNotifierID(noti.getNotifierID());
            notiBean.setEventID(noti.getEventID());
            notiBean.setNotificationID(noti.getNotificationID());
            notiBeanList.add(notiBean);
        }
        return notiBeanList;
    }

    public void deleteNotification(Integer notificationID, ArrayList<BNotification> notificationsList, int index) {
        //cancellazione nel DB
        this.notificationDAO.deleteNotification(notificationID);
        //rimozione dalla lista
        notificationsList.remove(index);
    }
}
