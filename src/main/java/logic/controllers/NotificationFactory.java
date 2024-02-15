package logic.controllers;

import logic.model.Notification;
import logic.model.ServerNotification;
import logic.utils.NotificationTypes;
import logic.utils.SituationType;
import logic.utils.UserTypes;

public class NotificationFactory {
    //questa factory crea i 2 tipi di notifiche: ServerClient e Local

    public Notification createNotification(SituationType sitType, NotificationTypes msgType, Integer clientID, Integer eventID, String city, UserTypes usrType){
        Notification noti;
        if (sitType == SituationType.ServerClient){
            noti = new ServerNotification();
        }else{
            noti = null; //SOSTITUIRE null CON GroupChatMessage
            //Implementazione in caso di groupchat

            //RAGIONAMENTO
            //SituationType sitType, NotificationTypes msgType, Integer clientID, Integer notifierID, String text, Integer eventID, Integer notificationID, String city, UserTypes usrType
            //clientID -> notifiedID nel Database, eventID -> groupID in caso di groupMessage
        }
        noti.setNotificationType(msgType);
        noti.setClientID(clientID);
        noti.setEventID(eventID);
        noti.setCity(city);
        noti.setUserType(usrType);
        return noti;
    }

}
