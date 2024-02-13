package logic.controllers;

import logic.model.Message;
import logic.model.NotificationMessage;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;
import logic.utils.SituationType;
import logic.utils.UserTypes;

public class MessageFactory {
    //questa factory si occupa della creazione dei messaggi che vengono scambiati tra client e server (non chat)

    public Message createMessage(SituationType sitType, MessageTypes msgType, Integer clientID, Integer eventID, String city, UserTypes usrType){
        Message msg;
        if (sitType == SituationType.Notification){
            msg = new NotificationMessage();
        }else{
            msg = null; //SOSTITUIRE null CON GroupChatMessage
            //Implementazione in caso di groupchat
        }
        msg.setMessageType(msgType);
        msg.setClientID(clientID);
        msg.setEventID(eventID);
        msg.setCity(city);
        msg.setUserType(usrType);
        return msg;
    }
}
