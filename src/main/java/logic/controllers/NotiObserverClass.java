package logic.controllers;

import logic.model.ServerNotification;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;

import static logic.view.EssentialGUI.logger;

public class NotiObserverClass extends ObserverClass{
    private NotificationFactory notiFactory = new NotificationFactory();
    @Override
    public void update(Object noti) {
        if (noti instanceof ServerNotification){
            try {
                out.writeObject(noti);
                out.flush();
                out.reset();
            }catch (InvalidClassException e){
                //gestione errore di serializzazione (writeObject)
                logger.severe("Cannot deserialize object");
            } catch (IOException e) {
                //gestione eccezioni IO
                logger.severe("Update notify error in ObsClass: " + e.getMessage());
            }
        }else{
            //TODO: Gestire errore se noti non e' del tipo corretto
            System.out.println("Errore nel tipo di oggetto - deve essere Notification");
        }

    }
}
