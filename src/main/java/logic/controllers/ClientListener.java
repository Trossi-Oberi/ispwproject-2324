package logic.controllers;

import logic.model.Notification;
import logic.utils.LoggedUser;
import logic.utils.SecureObjectInputStream;
import logic.utils.UserTypes;
import logic.view.NotificationView;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class ClientListener extends Thread implements Runnable{
    //il client listener si occupa di gestire la comunicazione in input da server a client
    private Semaphore semaphore;
    private SecureObjectInputStream in;
    private boolean listenerRunning = true;
    private NotificationView notificationView;

    public ClientListener(NotificationView notiView, Semaphore semaphore, SecureObjectInputStream in){
        this.notificationView = notiView;
        this.semaphore = semaphore;
        this.in = in;
    }



    @Override
    public void run() {
        try {
            semaphore.acquire();

            //una volta aperta socket e canali di comunicazione aspetto l'arrivo di un qualunque messaggio dal server
            while (listenerRunning) {
                Notification incomingMsg = (Notification) in.readObject();
                if (incomingMsg != null) {
                    switch (incomingMsg.getNotificationType()) {
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
                            if (LoggedUser.getUserType() == UserTypes.ORGANIZER) {
                                //rilascia semaforo solo per organizer per non bloccare l'applicazione
                                semaphore.release(2);
                            } else {

                                //chiama la giusta funzione in base alla GUI per lo user
                                notificationView.showNotification(incomingMsg.getNotificationType());
                            }
                            break;

                        case EventDeleted:
                            //rilascia semaforo solo per organizer per non bloccare l'applicazione
                            semaphore.release(2);
                            System.out.println("SERVER: Event " + incomingMsg.getEventID() + " deleted successfully!");
                            break;

                        case UserEventParticipation:
                            if (LoggedUser.getUserType() == UserTypes.USER) {
                                semaphore.release(2);
                            } else {
                                //chiama la giusta funzione in base alla GUI per notificare l'organizer
                                notificationView.showNotification(incomingMsg.getNotificationType());
                            }
                            break;

                        case UserEventRemoval:
                            semaphore.release(2);
                            System.out.println("SERVER: user " + incomingMsg.getClientID() + " removed participation to event " + incomingMsg.getEventID() + " successfully!");
                            break;

                        case GroupJoin:
                            System.out.println("SERVER: group with id " + incomingMsg.getEventID() + " joined successfully");
                            semaphore.release(2);
                            break;

                        case GroupLeave:
                            System.out.println("SERVER: user " + LoggedUser.getUserID() + " left group " + incomingMsg.getEventID());
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

        } catch (ClassNotFoundException | IOException e) {
            //gestione eccezioni di deserializzazione (readObject)
            logger.severe("Cannot serialize object");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e){
            //gestione rilascio semafori
            logger.severe(e.getMessage());
        }
    }
}
