package logic.model;

import logic.utils.MessageTypes;
import logic.utils.UserTypes;

import java.io.Serializable;

public interface Message extends Serializable {

    void setMessageType(MessageTypes msgType);

    void setCity(String city);

    void setClientID(Integer clientID);

    void setEventID(Integer eventID);

    void setUserType(UserTypes userType);


    MessageTypes getMessageType();

    String getCity();

    Integer getClientID();

    Integer getEventID();

    UserTypes getUserType();
}



