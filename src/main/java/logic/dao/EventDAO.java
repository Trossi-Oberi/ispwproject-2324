package logic.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import logic.controllers.ObserverClass;
import logic.exceptions.DuplicateEventParticipation;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;
import logic.model.MEvent;

import static logic.view.EssentialGUI.logger;

public class EventDAO {
    public void createEvent(MEvent eventModel) { //restituisce l'event id dell'evento appena aggiunto
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Events(event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1,eventModel.getEventOrganizer());
            statement.setInt(2, LoggedUser.getUserID()); //preso dalla sessione (classe LoggedUser)
            statement.setString(3,eventModel.getEventName());
            statement.setString(4,eventModel.getEventCity());
            statement.setString(5,eventModel.getEventAddress());
            statement.setString(6,eventModel.getEventMusicGenre());
            statement.setString(7,eventModel.getEventDate());
            statement.setString(8,eventModel.getEventTime());
            statement.setBytes(9,eventModel.getEventPicData());
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

        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public ArrayList<MEvent> retrieveMyEvents(int userID, int queryType){
        ArrayList<MEvent> myEvents = new ArrayList<>();

        if(queryType == 0) { //ORGANIZER && YourEventsOrg
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image FROM events WHERE (organizer_id = ?)")) {
                statement.setInt(1, userID);
                myEvents = getEventsArrayList(statement);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

        } else if(queryType == 1){   //USER && HomeUser
            UserDAO userDAO = new UserDAO();
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id,organizer,organizer_id,name,city,address,music_genre,date,time,image FROM events WHERE (city = ?)")) {
                statement.setString(1, userDAO.getUserCityByID(userID));
                myEvents = getEventsArrayList(statement);
            }catch (SQLException | RuntimeException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

        } else {  //USER && YourEventsUser
            UserDAO userDAO = new UserDAO();
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT events.* FROM Events JOIN UserEvent ON Events.event_id = UserEvent.event_id WHERE (UserEvent.user_id = ?)")){
                statement.setInt(1, LoggedUser.getUserID()); //prendo lo user id dalla sessione;
                myEvents = getEventsArrayList(statement);
            }catch (SQLException | RuntimeException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return myEvents;
    }

    public ArrayList<MEvent> getEventsArrayList(PreparedStatement statement){
        ArrayList<MEvent> events = new ArrayList<>();
        //event_id,organizer,name,city,address,music_genre,date,time,image
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                MEvent eventModel = new MEvent();
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
            logger.log(Level.SEVERE, "Cannot get logged user");
        }
        return events;
    }


    public void joinUserToEvent(MEvent eventModel) throws DuplicateEventParticipation {
        checkPreviousParticipation(eventModel.getEventID());
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO userevent (id, user_id, event_id) VALUES (NULL, ?, ?)")) {
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventModel.getEventID());
            statement.execute();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    private void checkPreviousParticipation(int eventID) throws DuplicateEventParticipation {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT * FROM userevent WHERE (user_id = ? AND event_id = ?)")){
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                throw new DuplicateEventParticipation("Event already joined");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }


    public int getParticipationsToEvent(int id) {
        int res = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT COUNT(user_id) FROM userevent WHERE (event_id = ?)")) {
            statement.setInt(1, id); //id_evento preso come parametro
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return res;
    }

    //gestita dal server
    public void populateOrgByEventID(Map<Integer, ObserverClass> orgByEventID){
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT event_id, organizer_id FROM events")){
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ObserverClass orgObs = new ObserverClass(rs.getInt(2), null);
                orgByEventID.put(rs.getInt(1), orgObs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
