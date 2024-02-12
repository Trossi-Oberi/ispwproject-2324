package logic.controllers;

import logic.beans.BUserData;
import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.server.Server;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

public class CNotification {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private boolean active = false;

    private Socket client;

    private Semaphore semaphore;
    private ClientListener listener;
    private Thread listenerThread;
    private NotificationDAO notificationDAO;

    private ObjectInputStream in;

    private ObjectOutputStream out;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
    }

//    public void startListener(int id){
//
//
//    }
//
//    public void stopListener(){
//
//        //closeClient();
//    }

    public void sendRegMessage(int userID, String city) throws RuntimeException{
        try (Socket regClient = new Socket(Server.ADDRESS, Server.PORT)){
            setActive(true);
            this.semaphore = new Semaphore(1);

            //apriamo la socket e i canali in/out
            this.out = new ObjectOutputStream(regClient.getOutputStream());
            this.in = new ObjectInputStream(regClient.getInputStream());

            //avviamo il thread con il listener
            this.listener = new ClientListener(userID, this.semaphore, this, this.in);
            this.listenerThread = new Thread(listener);
            this.listenerThread.start();

            //mando il messaggio di registrazione al server
            if (listenerThread.isAlive()) {
                Message registrationMsg = new Message(MessageTypes.UserRegistration, userID, city);
                out.writeObject(registrationMsg);
                out.flush();
                out.reset();
            }

            //aspetto il rilascio del semaforo nel thread dopo aver ricevuto la response dal server
            semaphore.acquire(2);

            //chiudo il thread listener del client
            setActive(false);
            listenerThread.interrupt();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setActive(boolean val){
        this.active = val;
    }

//    public void closeClient(){
//        try {
//            this.in.close();
//            this.out.close();
//            this.client.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
