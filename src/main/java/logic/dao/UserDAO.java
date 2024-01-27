package logic.dao;

import logic.utils.SingletonDBSession;
import logic.model.MUser;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    public int checkLoginInfo(MUser usrMod) {
        int ret = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT firstName, lastName, dateOfBirth, gender, userType FROM users WHERE (username=? and password=?)")){
            statement.setString(1, usrMod.getUserName());
            statement.setString(2, usrMod.getPassword());
            ret = getLoggedUser(statement, usrMod);
        }
        catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "SQLException occurred during the fetch of credentials");
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return ret;
    }

    public int getLoggedUser(PreparedStatement statement, MUser usrMod) {
        //verifica il tipo di utente che siamo

        try(ResultSet rs = statement.executeQuery()){
            while(rs.next()) {
                usrMod.setFirstName(rs.getString(1));
                usrMod.setLastName(rs.getString(2));
                usrMod.setBirthDate(rs.getString(3));
                usrMod.setGender(rs.getString(4));
                usrMod.setUserType(rs.getString(5));
                return 1;
            }
        }
        catch(SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot get logged user");
        }
        return 0;
    }

    public void registerUser(MUser usrModel) {
        try(PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Users (id, username, password, firstName, lastName, dateOfBirth, gender, userType,  userStatus) VALUES(NULL,?,?,?,?,?,?,?,?)")){
            statement.setString(1, usrModel.getUserName());
            statement.setString(2, usrModel.getPassword());
            statement.setString(3, usrModel.getFirstName());
            statement.setString(4, usrModel.getLastName());
            statement.setString(5, usrModel.getBirthDate());
            statement.setString(6, usrModel.getGender());
            statement.setString(7, usrModel.getUserType());
            statement.setString(8, "Offline");
            statement.execute();
        } catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot add a new user");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

}
