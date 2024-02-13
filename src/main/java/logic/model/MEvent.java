package logic.model;

import logic.beans.BEvent;

public class MEvent {
    private int eventID;
    private String eventName;
    private String eventCity;
    private String eventAddress;
    private String eventMusicGenre;
    private String eventDate;
    private String eventTime;
    private byte[] eventPicData;
    private String eventOrganizer;
    private int eventOrganizerID;


    public MEvent(BEvent eventBean) {
        eventID = eventBean.getEventID();
        eventName = eventBean.getEventName();
        eventCity = eventBean.getEventCity();
        eventAddress = eventBean.getEventAddress();
        eventMusicGenre = eventBean.getEventMusicGenre();
        eventDate = eventBean.getEventDate();
        eventTime = eventBean.getEventTime();
        eventPicData = eventBean.getEventPicData();
        eventOrganizer = eventBean.getEventOrganizer();
        eventOrganizerID = eventBean.getEventOrganizerID();
    }

    public MEvent() {
    }

    public BEvent getEventInfo() {
        BEvent eventInfo = new BEvent();
        eventInfo.setEventID(eventID);
        eventInfo.setEventName(eventName);
        eventInfo.setEventCity(eventCity);
        eventInfo.setEventAddress(eventAddress);
        eventInfo.setEventMusicGenre(eventMusicGenre);
        eventInfo.setEventDate(eventDate);
        eventInfo.setEventTime(eventTime);
        eventInfo.setEventPicData(eventPicData); //data = array di bytes
        eventInfo.setEventOrganizer(eventOrganizer);
        eventInfo.setEventOrganizerID(eventOrganizerID);
        return eventInfo;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
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
    public void setEventOrganizerID(int eventOrgID){
        this.eventOrganizerID = eventOrgID;
    }
    public int getEventID(){return this.eventID;}
    public String getEventName(){return this.eventName;}
    public String getEventCity() {
        return eventCity;
    }
    public String getEventAddress(){return this.eventAddress;}
    public String getEventMusicGenre(){return this.eventMusicGenre;}
    public String getEventDate(){return this.eventDate;}
    public String getEventTime(){return this.eventTime;}
    public byte[] getEventPicData(){return this.eventPicData;}
    public String getEventOrganizer(){return this.eventOrganizer;}
    public int getEventOrganizerID(){return this.eventOrganizerID;}

}


