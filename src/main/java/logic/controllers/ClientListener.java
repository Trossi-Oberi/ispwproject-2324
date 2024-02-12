package logic.controllers;

import logic.model.Message;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class ClientListener extends Thread implements Runnable{
    //il client listener si occupa di gestire la comunicazione in input da server a client

    private int clientID;
    private CNotification notificationCtrl;
    private Semaphore semaphore;
    private ObjectInputStream in;
    private boolean listenerRunning = true;

    private static Logger logger = Logger.getLogger("NightPlan");

    public ClientListener(int id, Semaphore semaphore, CNotification notiController, ObjectInputStream in){
        this.clientID = id;
        this.semaphore = semaphore;
        this.notificationCtrl = notiController;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();

            //una volta aperta socket e canali di comunicazione aspetto l'arrivo di un qualunque messaggio dal server
            while(listenerRunning){
                Message incomingMsg = (Message) in.readObject();
                if(incomingMsg != null){
                    switch (incomingMsg.getType()){
                        case UserRegistration:
                            System.out.println("New client " + incomingMsg.getClientID() + " successfully registered.");
                            //chiudo i canali di comunicazione del client con il server
                            semaphore.release(2);
                            listenerRunning = false;
                            break;

                        case LoggedIn:
                            System.out.println("Client " + incomingMsg.getClientID() + " logged in successfully.");
                            //chiudo i canali di comunicazione del client con il server
                            semaphore.release(2);
                            break;

                        case EventAdded:
                            if(LoggedUser.getUserType() == UserTypes.ORGANIZER){
                                //rilascia semaforo solo per organizer per non bloccare l'applicazione
                                semaphore.release(2);
                            } else{
                                //nuova notifica (static)
                                System.out.println("New event in your city");
                                EssentialGUI.showNotification(incomingMsg.getType());
                            }
                            break;

                        case Disconnected:
                            System.out.println("Client " + incomingMsg.getClientID() + " disconnected successfully.");
                            //chiudo i canali di comunicazione del client con il server
                            semaphore.release(2);
                            listenerRunning = false;
                            break;
                    }
                }
            }
        } catch (InterruptedException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e){
            logger.severe(e.getMessage());
        }
    }
}
