package logic.controllers;

import logic.model.Message;
import logic.model.Notification;

import logic.utils.LoggedUser;
import logic.utils.SecureObjectInputStream;
import logic.utils.UserTypes;


import java.io.IOException;
import java.util.concurrent.Semaphore;

import static logic.view.EssentialGUI.logger;

public class ClientListener implements Runnable {
    //il client listener si occupa di gestire la comunicazione in input da server a client
    //TODO: risolvere bug chiusura inaspettata socket se cambio città all'utente (quando disconnetto mi da errore)
    private Semaphore semaphore;
    private SecureObjectInputStream in;
    private boolean listenerRunning = true;
    private CFacade facade;
    private static final String SERVER_USER = "SERVER: user ";

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
            logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            //gestione rilascio semafori
            logger.severe(()->"Illegal argument "+e.getMessage());
        }
    }

    private void handleNotification(Notification incomingNoti) {
        if (incomingNoti != null) {
            switch (incomingNoti.getNotificationType()) {
                case USER_REGISTRATION:
                    logger.info(() -> "SERVER: New client " + incomingNoti.getClientID() + " successfully registered.");
                    //chiudo i canali di comunicazione del client con il server
                    semaphore.release(2);
                    listenerRunning = false;
                    break;

                case LOGGED_IN:
                    logger.info(() -> "SERVER: Client " + incomingNoti.getClientID() + " logged in successfully.");
                    //chiudo i canali di comunicazione del client con il server
                    semaphore.release(2);
                    break;

                case EVENT_ADDED:
                    if (LoggedUser.getUserType() == UserTypes.ORGANIZER) {
                        //rilascia semaforo solo per organizer per non bloccare l'applicazione
                        semaphore.release(2);
                    } else {
                        //chiama la giusta funzione in base alla GUI per lo user
                        facade.showNotification(incomingNoti.getNotificationType());
                    }
                    break;

                case EVENT_DELETED:
                    //rilascia semaforo solo per organizer per non bloccare l'applicazione
                    semaphore.release(2);
                    logger.info(() -> "SERVER: Event " + incomingNoti.getEventID() + " deleted successfully!");
                    break;

                case USER_EVENT_PARTICIPATION:
                    if (LoggedUser.getUserType() == UserTypes.USER) {
                        semaphore.release(2);
                    } else {
                        //chiama la giusta funzione in base alla GUI per notificare l'organizer
                        facade.showNotification(incomingNoti.getNotificationType());
                    }
                    break;

                case CHANGE_CITY:

                    logger.info(() -> SERVER_USER + incomingNoti.getClientID() + " changed city from " + incomingNoti.getCity() + " to " + incomingNoti.getNewCity() + " successfully!");
                    semaphore.release(2);
                    break;

                case GROUP_JOIN:
                    logger.info(() -> "SERVER: group with id " + incomingNoti.getEventID() + " joined successfully");
                    semaphore.release(2);
                    break;

                case GROUP_LEAVE:
                    logger.info(() -> SERVER_USER + LoggedUser.getUserID() + " left group " + incomingNoti.getEventID());
                    semaphore.release(2);
                    break;

                case DISCONNECTED:
                    logger.info(() -> "Client " + incomingNoti.getClientID() + " disconnected successfully.");
                    //chiudo i canali di comunicazione del client con il server
                    listenerRunning = false;
                    semaphore.release(2);
                    break;
            }
        }
    }

    private void handleMessage(Message incomingMsg) {
        //IMPLEMENTATO SOLO GROUPMESSAGE QUINDI NON FACCIO CONTROLLI AGGIUNTIVI SUL TIPO DI MESSAGGIO
        logger.info(() -> "LISTENER: Ricevuto messaggio da id: "+incomingMsg.getSenderID()+
                ", verso gruppo con id: "+incomingMsg.getReceiverID()+", testo: "+incomingMsg.getMessage());
        if (facade.getChatGraphic()!=null){ //significa che mi trovo effettivamente sulla schermata della chat
            facade.addMessageToChat(incomingMsg);
        }
        CServerInteraction.semaphore.release(2);
    }
}
