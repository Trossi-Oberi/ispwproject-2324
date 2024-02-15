package logic.model;

import logic.utils.MessageTypes;
import logic.utils.UserTypes;

public class NotificationMessage implements Message{
    private static final long serialVersionUID = 1L;

    private MessageTypes messageType;
    private String city;
    private Integer clientID;
    private Integer eventID;
    private UserTypes userType;

    @Override
    public void setMessageType(MessageTypes msgType) {
        this.messageType = msgType;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    @Override
    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    @Override
    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

    @Override
    public MessageTypes getMessageType() {
        return this.messageType;
    }

    public String getCity(){
        return this.city;
    }

    public Integer getClientID(){
        return this.clientID;
    }

    public Integer getEventID(){
        return this.eventID;
    }

    public UserTypes getUserType(){
        return this.userType;
    }
}
