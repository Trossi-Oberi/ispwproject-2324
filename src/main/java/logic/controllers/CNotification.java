package logic.controllers;

import logic.dao.NotificationDAO;
import logic.model.Message;
import logic.server.Server;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;
import logic.utils.SecureObjectInputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class CNotification {
    //questo controller si occupa solo di rigirare notifiche ai graphic controller a seguito di interazioni con il listener
    private Semaphore semaphore;
    private Socket client;
    private boolean active = false;
    private ClientListener listener;
    private Thread listenerThread;
    private NotificationDAO notificationDAO;

    private SecureObjectInputStream in;

    private ObjectOutputStream out;

    public CNotification(){
        this.notificationDAO = new NotificationDAO();
    }

    private void startListener(int userID){
        try {
            client = new Socket(Server.ADDRESS, Server.PORT);
            setActive(true);
            this.semaphore = new Semaphore(1);

            //apriamo la socket e i canali in/out
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new SecureObjectInputStream(client.getInputStream());

            //avviamo il thread con il listener
            this.listener = new ClientListener(userID, this.semaphore, this, this.in);
            this.listenerThread = new Thread(listener);

            //starto il thread
            this.listenerThread.start();

            logger.info("Client " + userID + " listener started successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegMessage(int userID, String city) throws RuntimeException{
        try {
            //avvio il listener del client
            startListener(userID);

            //TODO: fare un sendMessage() generico che prenda i parametri messageType, etc...
            //mando il messaggio di registrazione al server
            if (listenerThread.isAlive()) {
                Message registrationMsg = new Message(MessageTypes.UserRegistration, userID, city);
                out.writeObject(registrationMsg);
                out.flush();
                out.reset();
            }

            //aspetto il rilascio del semaforo nel thread dopo aver ricevuto la response dal server
            semaphore.acquire(2);

            //chiudo il client listener thread
            stopListener(userID);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendLoginMsg(int userID, String city){
        try{
            //avvio il listener del client
            startListener(userID);

            if (listenerThread.isAlive()) {
                Message registrationMsg = new Message(MessageTypes.LoggedIn, userID, city, LoggedUser.getUserType());
                out.writeObject(registrationMsg);
                out.flush();
                out.reset();
            }

            //aspetto il rilascio del semaforo nel thread dopo aver ricevuto la response dal server
            semaphore.acquire(2);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAddEventMessage(int orgID, int eventID, String city){
        try{
            if (listenerThread.isAlive()) {
                Message newEventMsg = new Message(MessageTypes.EventAdded, orgID, eventID, city);
                out.writeObject(newEventMsg);
                out.flush();
                out.reset();

                //aspetto il rilascio del semaforo nel thread dopo aver ricevuto la response dal server
                semaphore.acquire(2);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendLogoutMsg(int userID){
        try{
            if (listenerThread.isAlive()) {
                Message registrationMsg = new Message(MessageTypes.Disconnected, userID);
                out.writeObject(registrationMsg);
                out.flush();
                out.reset();

                //aspetto il rilascio del semaforo nel thread dopo aver ricevuto la response dal server
                semaphore.acquire(2);
                //chiudo il thread
                stopListener(userID);
            } else {
                logger.severe("Listener thread already stopped");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void stopListener(int userID){
        //chiudo il thread listener del client
        try {
            setActive(false);
            listenerThread.interrupt();
            if (!client.isClosed()) {
                this.client.close();
                logger.info("Client " + userID + " socket closed successfully");
            }
        } catch (IOException e){
            logger.severe(e.getMessage());
        }
    }

    private void setActive(boolean value){
        this.active = value;
    }
}
