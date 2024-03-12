package logic.controllers.factory;

import logic.model.MessageObserverClass;
import logic.model.NotiObserverClass;
import logic.model.ObserverClass;
import logic.utils.enums.ObserverType;

import java.io.ObjectOutputStream;

public class ObserverFactory {
    public ObserverClass createObserver(ObserverType obsType, Integer id, ObjectOutputStream out) {
        ObserverClass obs;
        if (obsType == ObserverType.NOTI_OBSERVER) {
            obs = new NotiObserverClass();
        } else {
            obs = new MessageObserverClass();
        }
        obs.setObsID(id);
        obs.setOut(out);
        return obs;
    }
}
