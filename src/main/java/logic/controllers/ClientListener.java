package logic.controllers;

import logic.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
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
                            System.out.println("New client " + incomingMsg.getClientID() + " successfully registered in observersByCity hashmap in Server istance.");
                            System.out.println("Connection to server can be stopped right now.");
                            //chiudo i canali di comunicazione del client con il server
                            semaphore.release(2);
                            //clientListenerShutdown();
                            listenerRunning = false;
                            break;
                    }
                }
            }

            //rilascio il semaforo se il listener sta ancora funzionando
//            if(listenerRunning){
//
//                //interrompo il client listener thread
//                //notificationCtrl.stopListener();
//            }
        } catch (InterruptedException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e){
            logger.severe(e.getMessage());
        }
    }

/*    private void clientListenerShutdown(){
        try{
            //chiudo canale input
            in.close();
        }catch(IOException e){
            logger.log(Level.SEVERE, "IOException during clientShutdown: " + e.getMessage());
        }
    }*/

}
