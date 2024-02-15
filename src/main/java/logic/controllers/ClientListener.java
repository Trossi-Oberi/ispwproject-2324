package logic.controllers;

import logic.model.Notification;
import logic.utils.LoggedUser;
import logic.utils.SecureObjectInputStream;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class ClientListener extends Thread implements Runnable{
    //il client listener si occupa di gestire la comunicazione in input da server a client

    private int clientID;
    private CNotification notificationCtrl;
    private Semaphore semaphore;
    private SecureObjectInputStream in;
    private boolean listenerRunning = true;

    public ClientListener(int id, Semaphore semaphore, CNotification notiController, SecureObjectInputStream in){
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
                Notification incomingMsg = (Notification) in.readObject();
                if(incomingMsg != null){
                    switch (incomingMsg.getNotificationType()){
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
                                EssentialGUI.showNotification(incomingMsg.getNotificationType());
                            }
                            break;

                        case UserEventParticipation:
                            if (LoggedUser.getUserType() == UserTypes.USER){
                                semaphore.release(2);
                            }else{
                                //notifica popup per l'organizer
                                System.out.println("New user participation to your event");
                                EssentialGUI.showNotification(incomingMsg.getNotificationType());
                            }
                            break;

                        case EventDeleted:
                            System.out.println("SERVER: Evento "+incomingMsg.getEventID()+" cancellato con successo");
                            semaphore.release(2);
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
