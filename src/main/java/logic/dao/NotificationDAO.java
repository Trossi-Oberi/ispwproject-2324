package logic.dao;

import logic.model.MEvent;
import logic.model.NotificationMessage;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class NotificationDAO {
    public void addNotificationsToUser(int usrID, MessageTypes messageTypes, int eventID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Notifications VALUES (NULL, ?, ?, ?)")){
            statement.setInt(1, usrID);
            statement.setString(2, messageTypes.toString());
            statement.setInt(3, eventID);
            statement.executeQuery();
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot add notification to user", e);
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public ArrayList<NotificationMessage> getNotificationsByUserID(int usrID) {
        ArrayList<NotificationMessage> notifications = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT type, event_id FROM notifications WHERE (user_id = ?)")) {
            statement.setInt(1, usrID);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    notifications.add(new NotificationMessage(rs.getString(1), rs.getInt(2)));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot get notifications from db", e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return notifications;
    }

    public boolean deleteNotification(int obsID){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM Notifications WHERE ")){
            statement.executeQuery();
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot remove notification from database", e);
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return true;
    }


}
