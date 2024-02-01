package logic.controllers;

import logic.beans.BEvent;
import logic.dao.EventDAO;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CManageEvent {

    private EventDAO eventDAO;


    public CManageEvent() {
        eventDAO = new EventDAO();
    }

    public boolean addEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        eventDAO.createEvent(eventModel);
        return true;
    }

    /*
    Il retrieve degli eventi va distinto in 3 casi:
    1: organizer si trova su schermata YourEventsOrg e gli vengono mostrati gli eventi che ha pubblicato (sia passati che futuri) - query con campo organizer_id
    2: user si trova su schermata HomeUser e gli vengono mostrati gli eventi disponibili nella sua citta' - query con campo citta'
    3: user si trova su schermata YourEventsUser e gli vengono mostrati gli eventi passati e futuri a cui ha messo la partecipazione - query con relazione user_id e event_id
    */

    public ArrayList<BEvent> retrieveMyEvents(UserTypes usertype, String fxmlpage) {
        ArrayList<MEvent> myEvents;
        if (usertype == UserTypes.ORGANIZER && fxmlpage.equals("YourEventsOrg.fxml")) { //Caso 1:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 0);

        } else if (usertype == UserTypes.USER && fxmlpage.equals("HomeUser.fxml")) {    //Caso 2:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 1);
        } else {                                                                        //Caso 3:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 2);
        }
        return getEventBeansListFromModelsList(myEvents);
    }

    public void updateEvent(BEvent eventBean) {

    }

    public void deleteEvent(int eventID) {

    }

    private ArrayList<BEvent> getEventBeansListFromModelsList(ArrayList<MEvent> eventModelList){
        ArrayList<BEvent> myEventsBeans = new ArrayList<>();
        BEvent tempEventBean = new BEvent();
        for (int i = 0; i < eventModelList.size(); i++) {
            tempEventBean = eventModelList.get(i).getEventInfo();
            myEventsBeans.add(tempEventBean);
        }
        return myEventsBeans;
    }
}