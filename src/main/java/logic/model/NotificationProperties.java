package logic.model;

public class NotificationProperties {
    private Integer notifierID;
    private Integer notificationID;

    public NotificationProperties(Integer notifierID, Integer notificationID){
        this.notifierID = notifierID;
        this.notificationID = notificationID;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
    }

    public void setNotifierID(Integer notifierID) {
        this.notifierID = notifierID;
    }

    public Integer getNotificationID() {
        return this.notificationID;
    }

    public Integer getNotifierID() {
        return this.notifierID;
    }
}
