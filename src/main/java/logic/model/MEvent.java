package logic.model;
import logic.beans.BEvent;

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

    private int eventID;

    private int organizerID;


    public MEvent(BEvent eventBean) {
        eventName = eventBean.getEventName();
        eventCity = eventBean.getEventCity();
        eventAddress = eventBean.getEventAddress();
        eventMusicGenre = eventBean.getEventMusicGenre();
        eventDate = eventBean.getEventDate();
        eventTime = eventBean.getEventTime();
        //eventPicFile = eventBean.getEventPicFile();
        eventPicData = eventBean.getEventPicData();
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
        //eventInfo.setEventPicFile(eventPicFile);
        eventInfo.setEventPicData(eventPicData); //data = array di bytes
        return eventInfo;
    }




    /*public void updateEvent() {
        AccommodationDao dao = new AccommodationDao();
        dao.update(this.getInfo());
    }*/

    public void setEventID(int id) {
        this.eventID = id;
    }

    /*public long getID() {
        return this.id;
    }*/

    //event_id, organizer, organizer_id, name, city, address, musicgenre, date, time, image
    public String getEventOrganizer(){return this.eventOrganizer;}

    public String getEventName(){return this.eventName;}
    public String getEventCity() {
        return eventCity;
    }

    public String getEventAddress(){return this.eventAddress;}

    public String getEventMusicGenre(){return this.eventMusicGenre;}

    public String getEventDate(){return this.eventDate;}

    public String getEventTime(){return this.eventTime;}

    public byte[] getEventPicData(){return this.eventPicData;}

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
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

    public void setEventPicDataFromDB(byte[] picData){
        this.eventPicData = picData;
    }

    public void setEventOrganizer(String eventOrg){
        this.eventOrganizer = eventOrg;
    }

    public void setEventOrganizerID(int id){
        this.organizerID = id;
    }

}


