package logic.controllers;
import logic.beans.BEvent;
import logic.dao.EventDAO;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CManageEvent {

    private EventDAO eventDAO;


    public CManageEvent(){
        eventDAO = new EventDAO();
    }

    public void addEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        eventDAO.createEvent(eventModel.getEventInfo());
    }

    /*
    Il retrieve degli eventi va distinto in 3 casi:
    1: organizer si trova su schermata YourEventsOrg e gli vengono mostrati gli eventi che ha pubblicato (sia passati che futuri) - query con campo organizer_id
    2: user si trova su schermata HomeUser e gli vengono mostrati gli eventi disponibili nella sua citta' - query con campo citta'
    3: user si trova su schermata YourEventsUser e gli vengono mostrati gli eventi passati e futuri a cui ha messo la partecipazione - query con relazione user_id e event_id
    */

    public ArrayList<MEvent> retrieveMyEvents(UserTypes usertype, String fxmlpage){
        ArrayList<MEvent> myEvents = new ArrayList<>();
        if(usertype == UserTypes.ORGANIZER && fxmlpage.equals("YourEventsOrg.fxml")){
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 0);
        }else if(usertype == UserTypes.USER && fxmlpage.equals("HomeUser.fxml") ){
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 1);
        }else{
            //codice se sei USER e ti trovi su YourEventsUser
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 2);
        }

        return myEvents;
    }

    public void updateEvent(BEvent eventBean){

    }

    public void deleteEvent(int eventID){

    }
}
