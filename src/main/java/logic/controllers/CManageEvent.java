package logic.controllers;
import logic.beans.BEvent;
import logic.dao.EventDAO;
import logic.model.MEvent;

public class CManageEvent {

    private EventDAO eventDAO;

    public CManageEvent(){

    }

    public void addEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        EventDAO dao = new EventDAO();
        dao.createEvent(eventModel.getEventInfo());
    }

    public void retrieveEvent(){

    }

    public void updateEvent(BEvent eventBean){

    }

    public void deleteEvent(int eventID){

    }
}
