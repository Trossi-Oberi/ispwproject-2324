package logic.dao;

import logic.utils.SingletonDBSession;
import logic.model.MUser;
import logic.utils.UserTypes;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger logger = Logger.getLogger("NightPlan");

    private String userCity;

    public int checkLoginInfo(MUser usrMod) {
        int ret = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, firstName, lastName, dateOfBirth, gender, city, userType FROM users WHERE (username=? and password=?)")) {
            statement.setString(1, usrMod.getUserName());
            statement.setString(2, usrMod.getPassword());
            ret = getLoggedUser(statement, usrMod);
        } catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "SQLException occurred during the fetch of credentials");
        } finally {
            SingletonDBSession.getInstance().closeConn();
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
                usrMod.setCity(rs.getString(6));

                //conversione da stringa a UserType
                String usrtypeString = rs.getString(7);
                if (usrtypeString.equals(UserTypes.USER.toString())){
                    usrMod.setUserType(UserTypes.USER);
                }else{
                    usrMod.setUserType(UserTypes.ORGANIZER);
                }
                return 1;
            }
        } catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot get logged user");
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
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Users (id, username, password, firstName, lastName, dateOfBirth, gender, city, userType,  userStatus) VALUES(NULL,?,?,?,?,?,?,?,?,?)")) {
            statement.setString(1, usrModel.getUserName());
            statement.setString(2, usrModel.getPassword());
            statement.setString(3, usrModel.getFirstName());
            statement.setString(4, usrModel.getLastName());
            statement.setString(5, usrModel.getBirthDate());
            statement.setString(6, usrModel.getGender());
            statement.setString(7, usrModel.getCity());
            statement.setString(8, usrModel.getUserType().toString());
            statement.setString(9, "Offline");
            statement.execute();
        } catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot add a new user");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
