package logic.controllers;

import logic.beans.BNotification;
import logic.dao.NotificationDAO;
import logic.dao.NotificationDAOCSV;
import logic.dao.NotificationDAOJDBC;
import logic.model.CityData;
import logic.model.Notification;
import logic.model.NotificationProperties;
import logic.utils.*;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class CNotification extends CServerInteraction {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private static final SituationType NOTIFICATION = SituationType.SERVER_CLIENT;
    private NotificationDAO notificationDAO;
    private NotificationFactory notiFactory;


    public CNotification(CFacade facadeRef) {
        super();
        switch (PersistenceClass.getPersistenceType()) {
            case FILE_SYSTEM:
                this.notificationDAO = new NotificationDAOCSV();
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
        listenerThread = new Thread(listener) {

        };

        //setto il thread come demone affinché termini quando termina anche il thread principale
        listenerThread.setDaemon(true);
        //starto il thread
        listenerThread.start();

        logger.info(() -> "Client " + userID + " listener started successfully");
    }

    public void sendNotification(NotificationTypes notiType, Integer clientID, NotificationProperties notiProps, Integer eventID, CityData cityData, UserTypes usrType) {
        try {
            //se ListenerThread non è ancora stato inizializzato oppure è stato inizializzato ma è stato poi interrotto lo avvio
            if (listenerThread == null || !listenerThread.isAlive()) {
                startListener(clientID, facade);
            }
            //creo il messaggio e lo mando al server
            if (listenerThread.isAlive()) {
                ObjectOutputStream out = LoggedUser.getOutputStream();
                Notification noti = notiFactory.createNotification(NOTIFICATION, notiType, clientID, notiProps, eventID, cityData, usrType);
                out.writeObject(noti);
                out.flush();
                out.reset();
            }

            //aspetto sempre risposta del server
            semaphore.acquire(2);

            //Vedo il tipo di messaggio per decidere se chiudere il listener oppure no
            if (notiType == NotificationTypes.USER_REGISTRATION || notiType == NotificationTypes.DISCONNECTED) {
                stopListener(clientID);
            }

        } catch (InvalidClassException e) {
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object");
        } catch (InterruptedException e) {
            //gestione eccezioni IO o interruzione thread
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private void stopListener(int userID) {
        //chiudo il thread listener del client
        try {
            listenerThread.join();
            if (!LoggedUser.getSocket().isClosed()) {
                LoggedUser.getOutputStream().close();
                LoggedUser.getInputStream().close();
                LoggedUser.getSocket().close();
                logger.info(() -> "Client " + userID + " socket closed successfully");
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }catch (InterruptedException e){
            logger.severe(()->"Interrupted exception "+e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public List<BNotification> retrieveNotifications(int userID) {
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

    public boolean deleteNotification(Integer notificationID, List<BNotification> notificationsList, int index) {
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
