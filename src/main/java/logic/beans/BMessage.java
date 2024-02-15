package logic.beans;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

public class BMessage {

    //TODO: Fare Bean messaggi per gestire correttamente la visualizzazione delle notifiche in GCNotification
    private NotificationTypes messageType;
    private Integer clientID;
    private Integer eventID;
    private String city;
    private UserTypes userType;
    private String text; //Attributo usato soltanto dai GroupMessage

    //SETTERS
    public void setMessageType(NotificationTypes messageType) {
        this.messageType = messageType;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

    public void setText(String text) {
        this.text = text;
    }

    //GETTERS

    public NotificationTypes getMessageType() {
        return messageType;
    }

    public Integer getClientID() {
        return clientID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public String getCity() {
        return city;
    }

    public UserTypes getUserType() {
        return userType;
    }

    public String getText() {
        return text;
    }
}
