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
            logger.log(Level.SEVERE, "SQLException occurred while adding notification to user");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> notifications = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, type, event_id, notifier_id FROM notifications WHERE (notified_id = ?)")) {
            statement.setInt(1, usrID);
            ResultSet rs = statement.executeQuery();
            Notification msg;
            while (rs.next()) {
                if (rs.getString(2).equals(NotificationTypes.EventAdded.toString())) {
                    msg = notiFactory.createNotification(SituationType.Local, NotificationTypes.EventAdded, usrID, rs.getInt(4), rs.getInt(3), rs.getInt(1), null, null);
                } else {
                    msg = notiFactory.createNotification(SituationType.Local, NotificationTypes.UserEventParticipation, usrID, rs.getInt(4), rs.getInt(3), rs.getInt(1), null, null);
                }
                notifications.add(msg);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while fetching notifications by userID from db");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return notifications;
    }

    public boolean deleteNotification(int notificationID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM Notifications WHERE id=?")) {
            statement.setInt(1, notificationID);
            statement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while removing notification from db");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
