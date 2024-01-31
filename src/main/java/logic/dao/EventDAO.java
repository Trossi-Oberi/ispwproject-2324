package logic.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public ArrayList<MEvent> retrieveMyEvents(int user_id, int queryType){
        ArrayList<MEvent> myEvents = new ArrayList<>();

        switch(queryType){
            case 0: //ORGANIZER && YourEventsOrg
                try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT (event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image) FROM Events WHERE organizer_id = ?")) {
                    statement.setInt(1, user_id);
                    myEvents = getEventsArrayList(statement);
                }catch (SQLException e) {
                        //Logger.getLogger("NightPlan").log(Level.SEVERE, EXCEPTION);
                        Logger.getLogger("NightPlan").log(Level.SEVERE, e.getMessage());
                }
                break;

            case 1: //USER && HomeUser
                UserDAO userDAO = new UserDAO();
                try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT (event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image) FROM Events WHERE city = ?")) {
                    statement.setString(1, userDAO.getUserCityByID(user_id));
                    myEvents = getEventsArrayList(statement);
                }catch (SQLException e) {
                    //Logger.getLogger("NightPlan").log(Level.SEVERE, EXCEPTION);
                    Logger.getLogger("NightPlan").log(Level.SEVERE, e.getMessage());
                }
                break;

            /*case 2: //USER && YourEventsUser
                break;

            default: eccezione con alertpopup*/

        }



        return myEvents;
    }

    public ArrayList<MEvent> getEventsArrayList(PreparedStatement statement){
        MEvent eventModel = new MEvent();
        ArrayList<MEvent> events = new ArrayList<>();
        //event_id,organizer,name,city,address,music_genre,date,time,image
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                eventModel.setEventID(rs.getInt(1));
                eventModel.setEventOrganizer(rs.getString(2));
                eventModel.setEventOrganizerID(rs.getInt(3));
                eventModel.setEventName(rs.getString(4));
                eventModel.setEventCity(rs.getString(5));
                eventModel.setEventAddress(rs.getString(6));
                eventModel.setEventMusicGenre(rs.getString(7));
                eventModel.setEventDate(rs.getString(8));
                eventModel.setEventTime(rs.getString(9));
                eventModel.setEventPicDataFromDB(rs.getBytes(10));
                events.add(eventModel);
            }
        } catch (SQLException e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "Cannot get logged user");
        }
        return events;
    }


}
