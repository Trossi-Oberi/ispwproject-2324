package logic.beans;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

public class BNotification {

    //TODO: Fare Bean messaggi per gestire correttamente la visualizzazione delle notifiche in GCNotification
    private NotificationTypes messageType;
    private Integer clientID; //notifiedID
    private Integer notifierID;
    private Integer eventID;
    private Integer notificationID;
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

    public void setNotifierID(Integer notifierID) {
        this.notifierID = notifierID;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
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

    public Integer getNotifierID() {
        return notifierID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public Integer getNotificationID() {
        return notificationID;
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
