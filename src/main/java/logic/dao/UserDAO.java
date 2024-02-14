package logic.dao;

import logic.controllers.ObserverClass;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;
import logic.model.MUser;
import logic.utils.UserTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import static logic.view.EssentialGUI.logger;

public class UserDAO {
    private String userCity;

    //TODO: fare UserDAO in doppia versione FILESYSTEM (.csv) e JDBC
    //TODO: applicare per ogni DAO la chiusura di connection, statement e result set ogni volta

    public int checkLoginInfo(MUser usrMod, boolean isGoogleAccount) {
        int ret = 0;
        if (!isGoogleAccount) {
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, firstName, lastName, dateOfBirth, gender, province, city, userType FROM users WHERE (username=? and password=?)")) {
                statement.setString(1, usrMod.getUserName());
                statement.setString(2, usrMod.getPassword());
                ret = getLoggedUser(statement, usrMod);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException occurred during the fetch of credentials");
            } finally {
                SingletonDBSession.getInstance().closeConn();
            }
        } else {
            //Google Login, verifico la presenza nel db della email Google
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, firstName, lastName, dateOfBirth, gender, province, city, userType FROM users WHERE (username=?)")) {
                statement.setString(1, usrMod.getUserName());
                ret = getLoggedUser(statement, usrMod);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException occurred during the fetch of credentials");
            } finally {
                SingletonDBSession.getInstance().closeConn();
            }
        }
        return ret;
    }

    private int getLoggedUser(PreparedStatement statement, MUser usrMod) {
        //verifica il tipo di utente che siamo
        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                usrMod.setId(rs.getInt(1));
                usrMod.setFirstName(rs.getString(2));
                usrMod.setLastName(rs.getString(3));
                usrMod.setBirthDate(rs.getString(4));
                usrMod.setGender(rs.getString(5));
                usrMod.setProvince(rs.getString(6));
                usrMod.setCity(rs.getString(7));

                //conversione da stringa a UserType
                String usrtypeString = rs.getString(8);
                if (usrtypeString.equals(UserTypes.USER.toString())){
                    usrMod.setUserType(UserTypes.USER);
                }else{
                    usrMod.setUserType(UserTypes.ORGANIZER);
                }
                return 1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot get logged user");
        }
        return 0;
    }

    public String getUserCityByID(int usrId) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT city FROM users WHERE (id=?)")) {
            statement.setInt(1, usrId);
            try (ResultSet rs = statement.executeQuery()){
                rs.next();
                this.userCity = rs.getString(1);
            }
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            logger.log(Level.SEVERE, "SQLException in getUserCityByID " + e + " -- MSG: " + e.getSQLState());
        }
        return userCity;
    }

    public void registerUser(MUser usrModel) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Users (id, username, password, firstName, lastName, dateOfBirth, gender, province, city, userType,  userStatus) VALUES(NULL,?,?,?,?,?,?,?,?,?,?)")) {
            statement.setString(1, usrModel.getUserName());
            statement.setString(2, usrModel.getPassword());
            statement.setString(3, usrModel.getFirstName());
            statement.setString(4, usrModel.getLastName());
            statement.setString(5, usrModel.getBirthDate());
            statement.setString(6, usrModel.getGender());
            statement.setString(7, usrModel.getProvince());
            statement.setString(8, usrModel.getCity());
            statement.setString(9, usrModel.getUserType().toString());
            statement.setString(10, "Offline");
            statement.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot add a new user", e);
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public int getUserIDByUsername(String username){
        int userID = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id FROM users WHERE (username = ?)")){
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                userID = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return userID;
    }

    public void setStatus(int userID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("UPDATE users SET userStatus = ? WHERE id = ?")) {
            statement.setString(1, LoggedUser.getStatus().equals("Online") ? "Offline" : "Online");
            statement.setInt(2, userID);
            int res = statement.executeUpdate();
            if(res == 1){
                if(LoggedUser.getStatus().equals("Online")){
                    LoggedUser.setStatus("Offline");
                } else {
                    LoggedUser.setStatus("Online");
                }
            }
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            logger.log(Level.SEVERE, "SQLException in setOnline " + e + " -- MSG: " + e.getSQLState());
        }
    }

    //gestita dal server
    public void populateObsByCity(Map<String, List<ObserverClass>> obsByCity){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, city FROM users WHERE (userType = 'USER')")){
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ObserverClass usrObs = new ObserverClass(rs.getInt(1), null);
                obsByCity.computeIfAbsent(rs.getString(2), k -> new ArrayList<>()).add(usrObs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }


}
