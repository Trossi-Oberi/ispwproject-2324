package logic.controllers;

import logic.beans.BNotification;
import logic.dao.NotificationDAO;
import logic.dao.NotificationDAOCSV;
import logic.dao.NotificationDAOJDBC;
import logic.model.Notification;
import logic.utils.*;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class CNotification extends CServerInteraction {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private static final SituationType NOTIFICATION = SituationType.ServerClient;
    private NotificationDAO notificationDAO;
    private NotificationFactory notiFactory;


    public CNotification(CFacade facadeRef){
        switch (PersistenceClass.getPersistenceType()) {
            case FileSystem:
                try {
                    this.notificationDAO = new NotificationDAOCSV();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case JDBC:
            default:
                this.notificationDAO = new NotificationDAOJDBC();
                break;

        }
        this.notiFactory = new NotificationFactory();
        facade = facadeRef;
    }

    private static void startListener(int userID, CFacade facade) {
        semaphore = new Semaphore(1);
        //avviamo il thread con il listener
        listener = new ClientListener(facade, semaphore, LoggedUser.getInputStream());
        listenerThread = new Thread(listener){

        };

        //setto il thread come demone affinché termini quando termina anche il thread principale
        listenerThread.setDaemon(true);
        //starto il thread
        listenerThread.start();

        logger.info("Client " + userID + " listener started successfully");
    }

    public void sendNotification(NotificationTypes notiType, Integer clientID, Integer notifierID, Integer eventID, Integer notificationID, String city, String newCity, UserTypes usrType) {
        try {
            //se ListenerThread non è ancora stato inizializzato oppure è stato inizializzato ma è stato poi interrotto lo avvio
            if (listenerThread == null || listenerThread.isInterrupted()) {
                startListener(clientID, facade);
            }
            //creo il messaggio e lo mando al server
            if (listenerThread.isAlive()) {
                ObjectOutputStream out = LoggedUser.getOutputStream();
                Notification noti = notiFactory.createNotification(NOTIFICATION, notiType, clientID, notifierID, eventID, notificationID, city, newCity, usrType);
                out.writeObject(noti);
                out.flush();
                out.reset();
            }

            //aspetto sempre risposta del server
            semaphore.acquire(2);

            //Vedo il tipo di messaggio per decidere se chiudere il listener oppure no
            if (notiType == NotificationTypes.UserRegistration || notiType == NotificationTypes.Disconnected) {
                stopListener(clientID);
            }

        } catch (InvalidClassException e) {
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object");
        } catch (IOException | InterruptedException e) {
            //gestione eccezioni IO o interruzione thread
            throw new RuntimeException(e);
        }
    }

    private void stopListener(int userID) {
        //chiudo il thread listener del client
        try {
            listenerThread.interrupt();
            if (!LoggedUser.getSocket().isClosed()){
                LoggedUser.getSocket().close();
                logger.info("Client " + userID + " socket closed successfully");
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public ArrayList<BNotification> retrieveNotifications(int userID) {
        ArrayList<Notification> notifications = new ArrayList<>(this.notificationDAO.getNotificationsByUserID(userID));
        return makeBeanFromModel(notifications);
    }

    private ArrayList<BNotification> makeBeanFromModel(ArrayList<Notification> notifications) {
        BNotification notiBean;
        ArrayList<BNotification> notiBeanList = new ArrayList<>();
        for (Notification noti : notifications) {
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

    public boolean deleteNotification(Integer notificationID, ArrayList<BNotification> notificationsList, int index) {
        //cancellazione nel DB
        if (this.notificationDAO.deleteNotification(notificationID)) {
            //rimozione dalla lista
            notificationsList.remove(index);
            return true;
        } else {
            return false;
        }
    }
}
