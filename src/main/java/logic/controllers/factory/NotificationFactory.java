package logic.controllers.factory;

import logic.model.*;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.SituationType;
import logic.utils.enums.UserTypes;

public class NotificationFactory {
    //questa factory crea i 2 tipi di notifiche: ServerClient e Local

    public Notification createNotification(SituationType sitType, NotificationTypes notiType, Integer clientOrNotifiedID, NotificationProperties notiProps, Integer eventID, CityData cityData, UserTypes usrType) {
        Notification noti;
        if (sitType == SituationType.SERVER_CLIENT) {
            noti = new ServerNotification();
        } else {
            noti = new LocalNotification();
        }
        noti.setNotificationType(notiType);
        noti.setClientID(clientOrNotifiedID);
        if(notiProps != null) {
            noti.setNotifierID(notiProps.getNotifierID());
            noti.setNotificationID(notiProps.getNotificationID());
        }
        noti.setEventID(eventID);
        if(cityData != null) {
            noti.setCity(cityData.getCity());
            noti.setNewCity(cityData.getNewCity());
        }
        noti.setUserType(usrType);
        return noti;
    }

}
