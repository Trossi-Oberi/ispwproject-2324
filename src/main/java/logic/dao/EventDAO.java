package logic.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.utils.SingletonDBSession;
import logic.beans.BEvent;
import logic.model.MEvent;

public class EventDAO {

    public MEvent createEvent(BEvent eventInfo) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Events(id,organizer,name,city,address,music_genre,date,time,image) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)")){
//            statement.setLong(1, eventInfo.getID());
//            statement.setBinaryStream(2,eventInfo.getInputFile(), eventInfo.getFileLength());
            statement.setString(1,eventInfo.getEventOrganizer());
            statement.setString(2,eventInfo.getEventName());
            statement.setString(3,eventInfo.getEventCity());
            statement.setString(4,eventInfo.getEventAddress());
            statement.setString(5,eventInfo.getEventMusicGenre());
            statement.setString(6,eventInfo.getEventDate());
            statement.setString(7,eventInfo.getEventTime());
            statement.setBytes(8,eventInfo.getEventPicData());

            statement.execute();
        }
        catch (SQLException e) {
            //Logger.getLogger("NightPlan").log(Level.SEVERE, EXCEPTION);
            Logger.getLogger("NightPlan").log(Level.SEVERE, e.getMessage());
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return new MEvent(eventInfo);
    }
}
