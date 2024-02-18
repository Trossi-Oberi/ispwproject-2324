package logic.controllers;

import logic.interfaces.MessageObserver;
import logic.model.Message;
import logic.model.Notification;
import logic.utils.SituationType;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;

import static logic.view.EssentialGUI.logger;

public class MessageObserverClass extends ObserverClass implements MessageObserver {

    public MessageObserverClass(int id, ObjectOutputStream out) {
        super(id,out);
    }

    @Override
    public void update(Message message) {
        try {
            //devo rigirare esattamente lo stesso messaggio che ricevo
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (InvalidClassException e){
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object");
        } catch (IOException e) {
            //gestione eccezioni IO
            logger.severe("Update notify error in MessageObsClass: " + e.getMessage());
        }
    }
}
