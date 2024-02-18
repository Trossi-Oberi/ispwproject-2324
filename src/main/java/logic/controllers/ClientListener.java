package logic.controllers;

import logic.model.MGroupMessage;
import logic.model.Message;
import logic.model.Notification;
import logic.model.ServerNotification;
import logic.utils.LoggedUser;
import logic.utils.SecureObjectInputStream;
import logic.utils.UserTypes;
import logic.view.NotificationView;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class ClientListener extends Thread implements Runnable {
    //il client listener si occupa di gestire la comunicazione in input da server a client
    private Semaphore semaphore;
    private SecureObjectInputStream in;
    private boolean listenerRunning = true;
    private CFacade facade;

    public ClientListener(CFacade facade, Semaphore semaphore, SecureObjectInputStream in) {
        this.facade = facade;
        this.semaphore = semaphore;
        this.in = in;
    }


    @Override
    public void run() {
        try {
            semaphore.acquire();
            while (listenerRunning) { //questo ciclo while si interrompe non appena il Client chiude i suoi canali di comunicazione con il server
                //blocco il thread in lettura di un messaggio in arrivo dal server
                //una volta aperta socket e canali di comunicazione aspetto l'arrivo di un qualunque messaggio dal server
                Object object = in.readObject();
                if (object instanceof Notification) {
                    handleNotification((Notification) object);
                } else if (object instanceof Message) {
                    handleMessage((Message) object);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            //gestione eccezioni di deserializzazione (readObject)
            logger.severe("Cannot serialize object");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            //gestione rilascio semafori
            logger.severe(e.getMessage());
        }
    }

    private void handleNotification(Notification incomingNoti) {
        if (incomingNoti != null) {
            switch (incomingNoti.getNotificationType()) {
                case UserRegistration:
                    System.out.println("New client " + incomingNoti.getClientID() + " successfully registered.");
                    //chiudo i canali di comunicazione del client con il server
                    semaphore.release(2);
                    listenerRunning = false;
                    break;

                case LoggedIn:
                    System.out.println("Client " + incomingNoti.getClientID() + " logged in successfully.");
                    //chiudo i canali di comunicazione del client con il server
                    semaphore.release(2);
                    break;

                case EventAdded:
                    if (LoggedUser.getUserType() == UserTypes.ORGANIZER) {
                        //rilascia semaforo solo per organizer per non bloccare l'applicazione
                        semaphore.release(2);
                    } else {

                        //chiama la giusta funzione in base alla GUI per lo user
                        facade.showNotification(incomingNoti.getNotificationType());
                    }
                    break;

                case EventDeleted:
                    //rilascia semaforo solo per organizer per non bloccare l'applicazione
                    semaphore.release(2);
                    System.out.println("SERVER: Event " + incomingNoti.getEventID() + " deleted successfully!");
                    break;

                case UserEventParticipation:
                    if (LoggedUser.getUserType() == UserTypes.USER) {
                        semaphore.release(2);
                    } else {
                        //chiama la giusta funzione in base alla GUI per notificare l'organizer
                        facade.showNotification(incomingNoti.getNotificationType());
                    }
                    break;

                case UserEventRemoval:
                    semaphore.release(2);
                    System.out.println("SERVER: user " + incomingNoti.getClientID() + " removed participation to event " + incomingNoti.getEventID() + " successfully!");
                    break;

                case GroupJoin:
                    System.out.println("SERVER: group with id " + incomingNoti.getEventID() + " joined successfully");
                    semaphore.release(2);
                    break;

                case GroupLeave:
                    System.out.println("SERVER: user " + LoggedUser.getUserID() + " left group " + incomingNoti.getEventID());
                    semaphore.release(2);
                    break;

                case Disconnected:
                    System.out.println("Client " + incomingNoti.getClientID() + " disconnected successfully.");
                    //chiudo i canali di comunicazione del client con il server
                    semaphore.release(2);
                    listenerRunning = false;
                    break;
            }
        }
    }

    private void handleMessage(Message incomingMsg) {
        //IMPLEMENTATO SOLO GROUPMESSAGE QUINDI NON FACCIO CONTROLLI AGGIUNTIVI SUL TIPO DI MESSAGGIO
        System.out.println("LISTENER: Ricevuto messaggio da id: "+incomingMsg.getSenderID()+
                ", verso gruppo con id: "+incomingMsg.getReceiverID()+", testo: "+incomingMsg.getMessage());
        if (facade.getChatGraphic()!=null){ //significa che mi trovo effettivamente sulla schermata della chat
            facade.addMessageToChat(incomingMsg);
        }
        semaphore.release(2);
    }
}
