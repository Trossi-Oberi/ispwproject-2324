package logic.controllers;

import logic.dao.NotificationDAO;

import java.util.concurrent.Semaphore;

public class CNotification {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private boolean active;
    private ClientListener listener;
    private Thread listenerThread;
    private NotificationDAO notificationDAO;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
    }

    public void startListener(int id){
        setActive(true);
        Semaphore semaphore = new Semaphore(1);
        listener = new ClientListener(id, semaphore, this);
        listenerThread = new Thread(listener);
        listenerThread.start();
    }

    public void stopListener(){
        setActive(false);
        listenerThread.interrupt();
    }

    private void setActive(boolean val){
        this.active = val;
    }




}
