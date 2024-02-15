package logic.model;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

public class ServerNotification implements Notification {
    private static final long serialVersionUID = 1L;
    private NotificationTypes messageType;
    private String city;
    private Integer clientID;
    private Integer eventID;
    private UserTypes userType;
    private Integer notifierID;


    @Override
    public void setNotificationType(NotificationTypes msgType) {
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

    public void setNotifierID(Integer notifierID) {
        this.notifierID = notifierID;
    }

    @Override
    public NotificationTypes getNotificationType() {
        return this.messageType;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public Integer getClientID() {
        return this.clientID;
    }

    @Override
    public Integer getEventID() {
        return this.eventID;
    }


    @Override
    public UserTypes getUserType() {
        return this.userType;
    }

    public Integer getNotifierID() {
        return notifierID;
    }
}
