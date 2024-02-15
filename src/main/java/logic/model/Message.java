package logic.model;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

import java.io.Serializable;

public interface Message extends Serializable {

    void setMessageType(NotificationTypes msgType);

    void setCity(String city);

    void setClientID(Integer clientID);

    void setEventID(Integer eventID);

    void setUserType(UserTypes userType);


    NotificationTypes getMessageType();

    String getCity();

    Integer getClientID();

    Integer getEventID();

    UserTypes getUserType();
}



