package logic.dao;

import logic.beans.BGroup;
import logic.model.MGroup;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class GroupDAO {
    public MGroup retrieveGroupByEventID(int eventID) {
        MGroup group = new MGroup();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_id, group_name, owner_id FROM EventGroups WHERE (event_id = ?)")) {
            statement.setInt(1, eventID); //id_evento preso come parametro
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                group.setGroupID(rs.getInt(1));
                group.setGroupName(rs.getString(2));
                group.setEventID(eventID);
                group.setOwnerID(rs.getInt(3));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return group;
        //ATTENZIONE A FARE LE VERIFICHE SUCCESSIVAMENTE SE E' NULL
    }

    public boolean userInGroup(int userID, Integer groupID) {
        if (groupID==null){
            return false;
        }
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT * FROM UserGroup WHERE (user_id=? AND group_id=?)")) {
            statement.setInt(1, userID);
            statement.setInt(2, groupID);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public String getGroupName(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_name FROM eventgroups WHERE (group_id=?)")) {
            statement.setInt(1, groupID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());

        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return null;
    }


//TODO: gestione gruppo già esistente per un evento, gestione nome gruppo già preso, uscita utente dal gruppo, creazione gruppo, eliminazione gruppo (isOwner booleano)


}
