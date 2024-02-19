package logic.dao;

import logic.model.Notification;
import logic.utils.NotificationTypes;

import java.util.ArrayList;

public interface NotificationDAO {
    void addNotification(ArrayList<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID);
    ArrayList<Notification> getNotificationsByUserID(int usrID);
    boolean deleteNotification(int notificationID);
}
