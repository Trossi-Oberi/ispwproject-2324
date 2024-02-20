package logic.dao;

import logic.model.Notification;
import logic.utils.NotificationTypes;

import java.util.ArrayList;
import java.util.List;

public interface NotificationDAO {
    void addNotification(List<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID);
    ArrayList<Notification> getNotificationsByUserID(int usrID);
    boolean deleteNotification(int notificationID);
}
