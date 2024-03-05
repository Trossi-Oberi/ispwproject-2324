package logic.model;

import java.io.IOException;
import java.io.InvalidClassException;

import static logic.view.EssentialGUI.logger;

public class NotiObserverClass extends ObserverClass{
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
        } else{
            logger.severe("Object noti (parameter) must be Notification (ServerNotification)");
        }
    }
}
