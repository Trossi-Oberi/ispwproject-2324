package logic.controllers;

import logic.interfaces.Observer;
import logic.model.Message;
import logic.utils.NotificationTypes;
import logic.utils.SituationType;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static logic.view.EssentialGUI.logger;

public class ObserverClass implements Observer {
    private final int id; //generico, sia per User che per Organizer
    private ObjectOutputStream out;
    private MessageFactory msgFactory = new MessageFactory();

    public ObserverClass(int id, ObjectOutputStream out){
        this.id = id;
        this.out = out;
    }

    @Override
    public void update(NotificationTypes type){
        try {
            Message message = msgFactory.createMessage(SituationType.Notification, type, null, null, null, null);
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            logger.severe("Update notify error in ObsClass: "+ e.getMessage());
        }
    }

    public void setOut(ObjectOutputStream out){
        this.out = out;
    }
    public int getObsID(){
        return this.id;
    }
}
