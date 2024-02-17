package logic.dao;

import logic.model.MEvent;
import logic.model.MGroupMessage;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class ChatDAO {
    public ArrayList<MGroupMessage> retrieveGroupChat(Integer groupID) {
        ArrayList<MGroupMessage> chatMessages = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT sender_id,message FROM ChatGroup WHERE (group_id = ?)")) {
            statement.setInt(1, groupID);
            return getMessagesArrayList(statement);
        } catch (SQLException | RuntimeException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    private ArrayList<MGroupMessage> getMessagesArrayList(PreparedStatement statement) {
        ArrayList<MGroupMessage> gMessages = new ArrayList<>();
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
}
