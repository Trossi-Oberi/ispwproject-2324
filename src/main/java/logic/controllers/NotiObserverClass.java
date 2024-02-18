package logic.controllers;

import logic.interfaces.NotiObserver;
import logic.model.Notification;
import logic.utils.NotificationTypes;
import logic.utils.SituationType;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;

import static logic.view.EssentialGUI.logger;

public class NotiObserverClass extends ObserverClass implements NotiObserver {
    private NotificationFactory notiFactory = new NotificationFactory();

    public NotiObserverClass(int id, ObjectOutputStream out) {
        super(id,out);
    }

    @Override
    public void update(NotificationTypes type) {
        try {
            Notification notification = notiFactory.createNotification(SituationType.ServerClient, type, null, null, null, null, null, null);
            out.writeObject(notification);
            out.flush();
            out.reset();
        } catch (InvalidClassException e){
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object");
        } catch (IOException e) {
            //gestione eccezioni IO
            logger.severe("Update notify error in ObsClass: " + e.getMessage());
        }
    }
}
