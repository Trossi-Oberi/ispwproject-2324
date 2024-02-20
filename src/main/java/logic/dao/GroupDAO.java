package logic.dao;

import logic.controllers.ObserverClass;
import logic.controllers.ObserverFactory;
import logic.model.MGroup;
import logic.utils.LoggedUser;
import logic.utils.ObserverType;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class GroupDAO {
    public MGroup retrieveGroupByEventID(int eventID) {
        MGroup group = new MGroup();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_id, group_name, owner_id FROM EventGroups WHERE (event_id = ?)")) {
            statement.setInt(1, eventID); //id_evento preso come parametro
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    group.setGroupID(rs.getInt(1));
                    group.setGroupName(rs.getString(2));
                    group.setEventID(eventID);
                    group.setOwnerID(rs.getInt(3));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving group by event ID");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return group;
    }

    public boolean userInGroup(int userID, Integer groupID) {
        if (groupID==null){
            return false;
        }
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT * FROM UserGroup WHERE (user_id=? AND group_id=?)")) {
            statement.setInt(1, userID);
            statement.setInt(2, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while checking user in group");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public String getGroupName(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_name FROM eventgroups WHERE (group_id=?)")) {
            statement.setInt(1, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting group name");

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
            logger.log(Level.SEVERE, "SQLException occurred while creating group");
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return -1;
    }

    public boolean joinGroup(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO usergroup(id, user_id,group_id) VALUES (NULL, ?, ?)")){
            statement.setInt(1,LoggedUser.getUserID());
            statement.setInt(2,groupID);
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while joining group");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public boolean leaveGroup(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM usergroup WHERE (user_id = ? AND group_id = ?)")){
            statement.setInt(1, LoggedUser.getUserID());
            statement.setInt(2, groupID);
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while leaving group");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public void populateUsersInGroups(Map<Integer, List<ObserverClass>> usersInGroups) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT user_id, group_id FROM usergroup")){
            ResultSet rs = statement.executeQuery();
            ObserverFactory obsFactory = new ObserverFactory();
            while (rs.next()) {
                ObserverClass obs = obsFactory.createObserver(ObserverType.MessageObserver, rs.getInt(1), null);
                usersInGroups.computeIfAbsent(rs.getInt(2), k -> new ArrayList<>()).add(obs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while populating UsersInGroups hashmap");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public boolean checkUserInGroup(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT 1 FROM usergroup WHERE (user_id = ? AND group_id = ?)")){
            statement.setInt(1, LoggedUser.getUserID());
            statement.setInt(2, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while checking user in group");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
