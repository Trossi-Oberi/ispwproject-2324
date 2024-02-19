package logic.controllers;

import logic.beans.BEvent;
import logic.dao.*;
import logic.exceptions.DuplicateEventParticipation;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.NotificationTypes;
import logic.utils.PersistenceClass;
import logic.utils.UserTypes;

import java.io.IOException;
import java.util.ArrayList;

import static logic.view.EssentialGUI.logger;

public class CManageEvent {

    private UserDAO userDAO;
    private EventDAO eventDAO;
    private UserEventDAO userEventDAO;
    private NotificationDAO notiDAO;


    public CManageEvent() {
        userDAO = new UserDAO();
        eventDAO = new EventDAO();
        userEventDAO = new UserEventDAO();
        switch (PersistenceClass.getPersistenceType()){
            case FileSystem:
                try {
                    notiDAO = new NotificationDAOCSV();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case JDBC:
            default:
                notiDAO = new NotificationDAOJDBC();
                break;
        }
    }

    public boolean addEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        if(eventDAO.createEvent(eventModel)){
            eventBean.setEventID(eventModel.getEventID());

            ArrayList<Integer> usersIDs;
            usersIDs = userDAO.getUsersInCity(eventBean.getEventCity());

            //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
            notiDAO.addNotification(usersIDs, NotificationTypes.EventAdded, eventBean.getEventID());
            return true;
        } else {
            return false;
        }
    }

    /*
    Il retrieve degli eventi va distinto in 3 casi:
    1: organizer si trova su schermata YourEventsOrg e gli vengono mostrati gli eventi che ha pubblicato (sia passati che futuri) - query con campo organizer_id
    2: user si trova su schermata HomeUser e gli vengono mostrati gli eventi disponibili nella sua citta' - query con campo citta'
    3: user si trova su schermata YourEventsUser e gli vengono mostrati gli eventi passati e futuri a cui ha messo la partecipazione - query con relazione user_id ed event_id
    */

    public ArrayList<BEvent> retrieveMyEvents(UserTypes usertype, String className) {
        ArrayList<MEvent> myEvents;
        if (usertype == UserTypes.ORGANIZER && className.equals("GCYourEventsOrg")) { //Caso 1:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 0);

        } else if (usertype == UserTypes.USER && className.equals("GCHomeUser")) {    //Caso 2:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 1);
        } else {                                                                        //Caso 3:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 2);
        }
        return getEventBeansListFromModelsList(myEvents);
    }

    public boolean editEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        return eventDAO.editEvent(eventModel);
    }

    public boolean deleteEvent(int eventID) {
        return eventDAO.deleteEvent(eventID);
    }

    public boolean participateToEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        if (userEventDAO.joinUserToEvent(eventModel)){
            //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente

            //notifico l'organizerID della partecipazione all'evento da parte dell'utente
            ArrayList<Integer> organizerID = new ArrayList<>();
            organizerID.add(eventBean.getEventOrganizerID());

            notiDAO.addNotification(organizerID, NotificationTypes.UserEventParticipation, eventBean.getEventID());
            return true;
        }else{
            return false;
        }
    }

    public boolean removeEventParticipation(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        return userEventDAO.removeUserToEvent(eventModel);
    }

    public boolean checkPreviousEventParticipation(BEvent eventBean){
        try {
            userEventDAO.checkPreviousParticipation(eventBean.getEventID());
            return false;
        } catch (DuplicateEventParticipation e) {
            return true;
        }
    }

    private ArrayList<BEvent> getEventBeansListFromModelsList(ArrayList<MEvent> eventModelList){
        ArrayList<BEvent> myEventsBeans = new ArrayList<>();
        BEvent tempEventBean;
        for (MEvent mEvent : eventModelList) {
            tempEventBean = mEvent.getEventInfo();
            myEventsBeans.add(tempEventBean);
        }
        return myEventsBeans;
    }

    public int getParticipationsToEvent(int id){
        return userEventDAO.getParticipationsToEvent(id);
    }

    public String getEventNameByEventID(int eventID) {
        return eventDAO.getEventNameByEventID(eventID);
    }


}
