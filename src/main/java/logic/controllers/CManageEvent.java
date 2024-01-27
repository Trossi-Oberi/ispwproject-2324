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
        /*if (eventModel.getID() != 0) {
            updateMyAccommodation(model);
        }
        else {
            try {
                rand = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
            model.setID(this.rand.nextInt(100000));*/
        eventModel.saveEvent();
        //}
    }
}
