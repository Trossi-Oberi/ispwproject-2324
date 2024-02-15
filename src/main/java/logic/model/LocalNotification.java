package logic.model;

import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

public class LocalNotification implements Notification{
    @Override
    public void setNotificationType(NotificationTypes msgType) {

    }

    @Override
    public void setCity(String city) {

    }

    @Override
    public void setClientID(Integer clientID) {

    }

    @Override
    public void setEventID(Integer eventID) {

    }

    @Override
    public void setUserType(UserTypes userType) {

    }

    @Override
    public NotificationTypes getNotificationType() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public Integer getClientID() {
        return null;
    }

    @Override
    public Integer getEventID() {
        return null;
    }

    @Override
    public UserTypes getUserType() {
        return null;
    }
}
