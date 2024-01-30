package logic.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;
import logic.beans.BEvent;
import logic.model.MEvent;

public class EventDAO {

    public MEvent createEvent(BEvent eventInfo) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Events(event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            statement.setString(1,eventInfo.getEventOrganizer());
            statement.setInt(2, LoggedUser.getUserID()); //preso dalla sessione (classe LoggedUser)
            statement.setString(3,eventInfo.getEventName());
            statement.setString(4,eventInfo.getEventCity());
            statement.setString(5,eventInfo.getEventAddress());
            statement.setString(6,eventInfo.getEventMusicGenre());
            statement.setString(7,eventInfo.getEventDate());
            statement.setString(8,eventInfo.getEventTime());
            statement.setBytes(9,eventInfo.getEventPicData());

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

   /* public ArrayList<MEvent> retrieveMyEvents(int organizer_id){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT (event_id,organizer,name,city,address,music_genre,date,time,image) FROM Events WHERE organizer_id = ?")){
            statement.setInt(1, organizer_id);
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
    }*/
}
