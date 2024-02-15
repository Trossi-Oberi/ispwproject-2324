package logic.dao;

import logic.controllers.MessageFactory;
import logic.model.Message;
import logic.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class NotificationDAO {
    private MessageFactory notiFactory;

    public NotificationDAO() {
        notiFactory = new MessageFactory();
    }

    public void addNotificationsToUser(int usrID, NotificationTypes notificationTypes, int eventID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Notifications VALUES (NULL, ?, ?, ?)")) {
            statement.setInt(1, usrID);
            statement.setString(2, notificationTypes.toString());
            statement.setInt(3, eventID);
            statement.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot add notification to user" + e.getMessage(), e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public ArrayList<Message> getNotificationsByUserID(int usrID) {
        ArrayList<Message> notifications = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT type, event_id FROM notifications WHERE (user_id = ?)")) {
            statement.setInt(1, usrID);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                Message msg;
                while (rs.next()) {
                    if (rs.getString(1).equals(NotificationTypes.EventAdded.toString())) {
                        msg = notiFactory.createMessage(SituationType.Notification, NotificationTypes.EventAdded, null, rs.getInt(2), null, null);

                    } else if (rs.getString(1).equals(NotificationTypes.UserEventParticipation.toString())) {
                        //implementazione UserParticipation
                        msg = notiFactory.createMessage(SituationType.Notification, NotificationTypes.UserEventParticipation, null, rs.getInt(2), null, null);
                    } else {
                        //TODO: implementazione NewMessageInGroupChat
                        msg = null; //DA CAMBIARE
                    }
                    notifications.add(msg);

                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot get notifications from db", e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return notifications;
    }

    public boolean deleteNotification(int obsID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM Notifications WHERE ")) {
            statement.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot remove notification from database", e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return true;
    }


}
