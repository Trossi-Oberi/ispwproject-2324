package logic.dao;

import logic.exceptions.DuplicateEventParticipation;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class UserEventDAO {

    public void joinUserToEvent(MEvent eventModel) throws DuplicateEventParticipation {
        checkPreviousParticipation(eventModel.getEventID());
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO userevent (id, user_id, event_id) VALUES (NULL, ?, ?)")) {
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventModel.getEventID());
            statement.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    private void checkPreviousParticipation(int eventID) throws DuplicateEventParticipation {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT * FROM userevent WHERE (user_id = ? AND event_id = ?)")) {
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                throw new DuplicateEventParticipation("Event already joined");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public int getParticipationsToEvent(int id) {
        int res = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT COUNT(user_id) FROM userevent WHERE (event_id = ?)")) {
            statement.setInt(1, id); //id_evento preso come parametro
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return res;
    }
}
