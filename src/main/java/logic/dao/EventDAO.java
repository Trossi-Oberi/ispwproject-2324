package logic.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import logic.controllers.ObserverClass;
import logic.utils.LoggedUser;
import logic.utils.PersistenceClass;
import logic.utils.SingletonDBSession;
import logic.model.MEvent;

import static logic.view.EssentialGUI.logger;

public class EventDAO {
    public boolean createEvent(MEvent eventModel) { //restituisce l'event id dell'evento appena aggiunto
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Events(event_id,organizer,organizer_id,name,province, city,address,music_genre,date,time,image, pic_path) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1,eventModel.getEventOrganizer());
            statement.setInt(2, LoggedUser.getUserID()); //preso dalla sessione (classe LoggedUser)
            statement.setString(3,eventModel.getEventName());
            statement.setString(4, eventModel.getEventProvince());
            statement.setString(5,eventModel.getEventCity());
            statement.setString(6,eventModel.getEventAddress());
            statement.setString(7,eventModel.getEventMusicGenre());
            statement.setString(8,eventModel.getEventDate());
            statement.setString(9,eventModel.getEventTime());
            statement.setBytes(10,eventModel.getEventPicData());
            statement.setString(11,eventModel.getEventPicPath());
            int insertedRows = statement.executeUpdate();

            //recupero id evento dopo l'aggiunta
            if (insertedRows>0){
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerato = rs.getInt(1);
                        eventModel.setEventID(idGenerato);
                    }
                }
            }
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while creating new event");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public boolean editEvent(MEvent eventModel){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("UPDATE Events SET name=?,province=?,city=?,address=?,music_genre=?,date=?,time=?,image=?,pic_path=? WHERE event_id=?")){
            statement.setString(1,eventModel.getEventName());
            statement.setString(2,eventModel.getEventProvince());
            statement.setString(3,eventModel.getEventCity());
            statement.setString(4,eventModel.getEventAddress());
            statement.setString(5,eventModel.getEventMusicGenre());
            statement.setString(6,eventModel.getEventDate());
            statement.setString(7,eventModel.getEventTime());
            statement.setBytes(8,eventModel.getEventPicData());
            statement.setString(9,eventModel.getEventPicPath());
            statement.setInt(10,eventModel.getEventID()); //ID Evento da aggiornare
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while editing event");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }

    }

    public boolean deleteEvent(int eventID){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM Events WHERE event_id=?")){
            statement.setInt(1,eventID); //ID Evento da aggiornare
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while deleting event");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }

    }

    public ArrayList<MEvent> retrieveMyEvents(int userID, int queryType){
        ArrayList<MEvent> myEvents = new ArrayList<>();

        if(queryType == 0) { //ORGANIZER && YourEventsOrg
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id,organizer,organizer_id,name,province,city,address,music_genre,date,time,image,pic_path FROM events WHERE (organizer_id = ?)")) {
                statement.setInt(1, userID);
                myEvents = getEventsArrayList(statement);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException occurred while retrieving events");
            }
        } else if(queryType == 1){   //USER && HomeUser
            UserDAO userDAO;
            switch (PersistenceClass.getPersistenceType()){
                case FileSystem:
                    try {
                        userDAO = new UserDAOCSV();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case JDBC:
                default:
                    userDAO = new UserDAOJDBC();
                    break;
            }
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id,organizer,organizer_id,name,province,city,address,music_genre,date,time,image,pic_path FROM events WHERE (city = ?)")) {
                statement.setString(1, userDAO.getUserCityByID(userID));
                myEvents = getEventsArrayList(statement);
            }catch (SQLException | RuntimeException e) {
                logger.log(Level.SEVERE, "SQLException occurred while retrieving events");
            }

        } else {  //USER && YourEventsUser
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT events.* FROM Events JOIN UserEvent ON Events.event_id = UserEvent.event_id WHERE (UserEvent.user_id = ?)")){
                statement.setInt(1, LoggedUser.getUserID()); //prendo lo user id dalla sessione;
                myEvents = getEventsArrayList(statement);
            }catch (SQLException | RuntimeException e) {
                logger.log(Level.SEVERE, "SQLException occurred while retrieving events");
            }
        }

        return myEvents;
    }

    public ArrayList<MEvent> getEventsArrayList(PreparedStatement statement){
        ArrayList<MEvent> events = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                MEvent eventModel = new MEvent();
                eventModel.setEventID(rs.getInt(1));
                eventModel.setEventOrganizer(rs.getString(2));
                eventModel.setEventOrganizerID(rs.getInt(3));
                eventModel.setEventName(rs.getString(4));
                eventModel.setEventProvince(rs.getString(5));
                eventModel.setEventCity(rs.getString(6));
                eventModel.setEventAddress(rs.getString(7));
                eventModel.setEventMusicGenre(rs.getString(8));
                eventModel.setEventDate(rs.getString(9));
                eventModel.setEventTime(rs.getString(10));
                eventModel.setEventPicDataFromDB(rs.getBytes(11));
                eventModel.setEventPicPath(rs.getString(12));
                events.add(eventModel);
            }
            return events;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot retrieve events");
            return null;
        } finally{
            SingletonDBSession.getInstance().closeConn();
        }
    }


    //gestita dal server
    public void populateOrgByEventID(Map<Integer, ObserverClass> orgByEventID){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id, organizer_id FROM events")){
            try(ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ObserverClass orgObs = new ObserverClass(rs.getInt(2), null);
                    orgByEventID.put(rs.getInt(1), orgObs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while populating orgByEventID hashmap");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public String getEventNameByEventID(int eventID) {
        String name = null;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT name FROM events WHERE (event_id = ?)")) {
            statement.setInt(1, eventID); //id_evento preso come parametro
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    name = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting event name by event ID");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return name;
    }
}
