package logic.dao;

import logic.utils.SingletonDBSession;
import logic.model.MUser;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    public int checkLogInInfo(MUser usrMod) {
        int ret = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT typeOfUser FROM users WHERE (username=? and password=?)")){
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

    /*
    public void insertNewUser(MUser usrModel) {
        PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO usr(username, passw, nome, surname, dateOfBirth, gender, tipeOfUser, profilePicture, userStatus) VALUES(?,?,?,?,?,?,?,?,?)"))
            statement.setString(1, usrModel.getUserName());
            statement.setString(2, usrModel.getPaswd());
            statement.setString(3, usrModel.getName());
            statement.setString(4, usrModel.getSurname());
            statement.setDate(5, Date.valueOf(usrModel.getDateOfBirth()));
            statement.setString(6, usrModel.getGender());
            statement.setString(7, usrModel.getUserType());
            statement.setBinaryStream(8, usrModel.getInputFile(), usrModel.getFileLength());
            statement.setString(9, "offline");
            statement.execute();
        }
        finally {
            SingletonDbConnection.getInstance().closeConn();
        }
    }
*/

    public int getLoggedUser(PreparedStatement statement, MUser usrMod) {
        //verifica il tipo di utente che siamo

        try(ResultSet rs = statement.executeQuery()){
            while(rs.next()) {
                usrMod.setLogUsrCred(rs.getString(1));
                return 1;
            }
        }
        catch(SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot get logged user");
        }
        return 0;
    }

}
