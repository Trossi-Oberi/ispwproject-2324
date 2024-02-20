package logic.controllers;

import logic.model.Message;

import java.io.IOException;
import java.io.InvalidClassException;

import static logic.view.EssentialGUI.logger;

public class MessageObserverClass extends ObserverClass {

    @Override
    public void update(Object message) {
        if (message instanceof Message) {
            try {
                //devo rigirare esattamente lo stesso messaggio che ricevo
                out.writeObject(message);
                out.flush();
                out.reset();
            } catch (InvalidClassException e) {
                //gestione errore di serializzazione (writeObject)
                logger.severe("Cannot deserialize object");
            } catch (IOException e) {
                //gestione eccezioni IO
                logger.severe("Update notify error in MessageObsClass: " + e.getMessage());
            }
        } else {
            logger.severe("Object message (parameter) must be Message");
        }

    }
}
