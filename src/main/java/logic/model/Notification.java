package logic.model;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

import java.io.Serializable;

public interface Notification extends Serializable {


    void setNotificationType(NotificationTypes msgType);

    void setCity(String city);

    void setClientID(Integer notifiedID);

    void setNotifierID(Integer notifierID);

    void setEventID(Integer eventID);

    void setNotificationID(Integer notificationID);

    void setUserType(UserTypes userType);


    NotificationTypes getNotificationType();

    String getCity();

    Integer getClientID();

    Integer getNotifierID();

    Integer getEventID();

    Integer getNotificationID();

    UserTypes getUserType();

    //NotificationTypes notiType, Integer clientID/notifiedID, Integer notifierID, Integer notificationID, Integer eventID, String city, UserTypes usrType
}



