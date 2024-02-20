package logic.dao;

import logic.model.MGroupMessage;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class ChatDAO {
    public List<MGroupMessage> retrieveGroupChat(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT sender_id,message FROM ChatGroup WHERE (group_id = ?)")) {
            statement.setInt(1, groupID);
            return getMessagesArrayList(statement);
        } catch (SQLException | RuntimeException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return new ArrayList<>(); //null - toglie smell
        }finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    private List<MGroupMessage> getMessagesArrayList(PreparedStatement statement) {
        List<MGroupMessage> gMessages = new ArrayList<>();
        //sender_id,message
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                MGroupMessage model = new MGroupMessage();
                model.setSenderID(rs.getInt(1));
                model.setMessage(rs.getString(2));
                gMessages.add(model);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot get logged user");
        }
        return gMessages;
    }

    public boolean writeMessage(Integer groupID, String text) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO chatgroup(id,sender_id, message, group_id) VALUES (NULL,?,?,?)")){
            statement.setInt(1,LoggedUser.getUserID());
            statement.setString(2,text);
            statement.setInt(3,groupID);
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
