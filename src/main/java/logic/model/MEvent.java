package logic.model;
import logic.beans.BEvent;
import logic.dao.EventDAO;
import logic.utils.LoggedUser;

import java.io.File;

public class MEvent {
    private String eventName;
    private String eventCity;
    private String eventAddress;
    private String eventMusicGenre;
    private String eventDate;
    private String eventTime;

    private File eventPicFile;
    private byte[] eventPicData;
    private String eventOrganizer;

    private int organizerID;


    public MEvent(BEvent eventBean) {
//        id = bean.getID();
        eventName = eventBean.getEventName();
        eventCity = eventBean.getEventCity();
        eventAddress = eventBean.getEventAddress();
        eventMusicGenre = eventBean.getEventMusicGenre();
        eventDate = eventBean.getEventDate();
        eventTime = eventBean.getEventTime();
        if (eventBean.getEventPicFile()!=null){
            eventPicData = eventBean.getEventPicData();
        }
        eventOrganizer = eventBean.getEventOrganizer();
    }

    public MEvent() {
    }

    public BEvent getEventInfo() {
        BEvent eventInfo = new BEvent();
//        eventInfo.setID(id);
        eventInfo.setEventName(eventName);
        eventInfo.setEventOrganizer(eventOrganizer);
        eventInfo.setEventCity(eventCity);
        eventInfo.setEventAddress(eventAddress);
        eventInfo.setEventMusicGenre(eventMusicGenre);
        eventInfo.setEventDate(eventDate);
        eventInfo.setEventTime(eventTime);
        eventInfo.setEventPicData(eventPicFile); //data = array di bytes
        return eventInfo;
    }




    /*public void updateEvent() {
        AccommodationDao dao = new AccommodationDao();
        dao.update(this.getInfo());
    }*/

    /*public void setID(long id) {
        this.id = id;
    }*/

    /*public long getID() {
        return this.id;
    }*/

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = getEventCity(eventCity);
    }

    public String getEventCity(String eventCity) {
        return eventCity;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public void setEventMusicGenre(String eventMusicGenre) {
        this.eventMusicGenre = eventMusicGenre;
    }
    public void setEventDate(String eventDate){
        this.eventDate = eventDate;
    }
    public void setEventTime(String eventTime){
        this.eventTime = eventTime;
    }

    public void setEventPic(){

    }

}


