package logic.model;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

import java.io.Serializable;

public interface Notification extends Serializable {

    void setNotificationType(NotificationTypes msgType);

    void setCity(String city);

    void setClientID(Integer clientID);

    void setEventID(Integer eventID);

    void setUserType(UserTypes userType);


    NotificationTypes getNotificationType();

    String getCity();

    Integer getClientID();

    Integer getEventID();

    UserTypes getUserType();
}



