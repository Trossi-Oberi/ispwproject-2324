package logic.model;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class ServerNotification implements Notification {
    private static final long serialVersionUID = 1L;
    private NotificationTypes messageType;
    private String city;
    private String newCity;
    private Integer clientID;
    private Integer eventID;
    private UserTypes userType;


    @Override
    public void setNotificationType(NotificationTypes msgType) {
        this.messageType = msgType;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void setNewCity(String newCity){
        this.newCity = newCity;
    }

    @Override
    public void setClientID(Integer notifiedID) {
        this.clientID = notifiedID;
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
    public void setNotifierID(Integer notifierID) {
        //empty
    }

    @Override
    public void setNotificationID(Integer notificationID){
        //empty
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
    public String getNewCity(){
        return this.newCity;
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


    @Override
    public Integer getNotifierID() {
        return -1;
    }

    @Override
    public Integer getNotificationID(){
        return -1;
    }
}
