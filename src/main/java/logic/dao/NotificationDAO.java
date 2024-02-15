package logic.dao;

import logic.controllers.NotificationFactory;
import logic.model.Notification;
import logic.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class NotificationDAO {
    private NotificationFactory notiFactory;

    public NotificationDAO() {
        notiFactory = new NotificationFactory();
    }

    public void addNotification(int notifiedID, NotificationTypes notificationTypes, int eventID, int notifierID) {
        //questo metodo ha come valore di ritorno l'id della notifica appena inserita nel database
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Notifications VALUES (NULL, ?, ?, ?, ?)")) {
            statement.setInt(1, notifiedID);
            statement.setString(2, notificationTypes.toString());
            statement.setInt(3, eventID);
            statement.setInt(4, notifierID);
            statement.execute();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot add notification to user" + e.getMessage(), e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> notifications = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT type, event_id, notifier_id FROM notifications WHERE (notified_id = ?)")) {
            statement.setInt(1, usrID);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                Notification msg;
                while (rs.next()) {
                    if (rs.getString(1).equals(NotificationTypes.EventAdded.toString())) {
                        msg = notiFactory.createNotification(SituationType.ServerClient, NotificationTypes.EventAdded, rs.getInt(3), rs.getInt(2), null, null);

                    } else if (rs.getString(1).equals(NotificationTypes.UserEventParticipation.toString())) {
                        //implementazione UserParticipation
                        msg = notiFactory.createNotification(SituationType.ServerClient, NotificationTypes.UserEventParticipation, rs.getInt(3), rs.getInt(2), null, null);
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
