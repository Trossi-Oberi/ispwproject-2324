package logic.dao;

import logic.beans.BGroup;
import logic.model.MGroup;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public int createGroup(String groupName, int eventID) { //returna l'id dell'evento creato
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO eventgroups(group_id, group_name, event_id, owner_id) VALUES (NULL, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1,groupName);
            statement.setInt(2,eventID);
            statement.setInt(3,LoggedUser.getUserID());

            int insertedRows = statement.executeUpdate();
            //recupero id evento dopo l'aggiunta
            if (insertedRows>0){
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return -1;
    }

    public void joinGroup(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO usergroup(id, user_id,group_id) VALUES (NULL, ?, ?)")){
            statement.setInt(1,LoggedUser.getUserID());
            statement.setInt(2,groupID);
            statement.execute();
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }


//TODO: gestione gruppo già esistente per un evento, gestione nome gruppo già preso, uscita utente dal gruppo, creazione gruppo, eliminazione gruppo (isOwner booleano)


}
