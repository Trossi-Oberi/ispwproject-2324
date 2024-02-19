package logic.controllers;

import logic.model.Message;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;

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
            //TODO: Gestione errore se message non e' di tipo Message
            System.out.println("Errore nel tipo di oggetto - deve essere Message");
        }

    }
}
