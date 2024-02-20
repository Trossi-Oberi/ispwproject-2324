package logic.controllers;

import logic.model.LocalNotification;
import logic.model.Notification;
import logic.model.ServerNotification;
import logic.utils.NotificationTypes;
import logic.utils.SituationType;
import logic.utils.UserTypes;

public class NotificationFactory {
    //questa factory crea i 2 tipi di notifiche: ServerClient e Local

    public Notification createNotification(SituationType sitType, NotificationTypes notiType, Integer clientOrNotifiedID, Integer notifierID, Integer eventID, Integer notificationID, String city, String newCity, UserTypes usrType) {
        Notification noti;
        if (sitType == SituationType.SERVER_CLIENT) {
            noti = new ServerNotification();
        } else {
            noti = new LocalNotification();
        }
        noti.setNotificationType(notiType);
        noti.setClientID(clientOrNotifiedID);
        noti.setNotifierID(notifierID);
        noti.setEventID(eventID);
        noti.setNotificationID(notificationID);
        noti.setCity(city);
        noti.setNewCity(newCity);
        noti.setUserType(usrType);
        return noti;
    }

}
