package logic.beans;

public class BEvent {
    private String eventName;
    private String eventCity;
    private String eventAddress;
    private String eventMusicGenre;
    private String eventDate;
    private String eventTime;
    private byte[] eventPicData;
    private String eventOrganizer;

    public BEvent() {
    }

    public void setEventName(String eventName){ //throws LengthFieldException, NullValueException{
        this.eventName = eventName;
    }

    public void setEventCity(String eventCity){ //throws LengthFieldException, NullValueException{
        this.eventCity = eventCity;
    }

    public void setEventAddress(String eventAddress){ //throws LengthFieldException, NullValueException{
        this.eventAddress = eventAddress;
    }

    public void setEventMusicGenre(String eventMusicGenre){ //throws LengthFieldException, NullValueException{
        this.eventMusicGenre = eventMusicGenre;
    }

    public void setEventDate(String eventDate){ //throws LengthFieldException, NullValueException{
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventHour, String eventMinutes){ //throws LengthFieldException, NullValueException{
        this.eventTime = eventHour+":"+eventMinutes;
    }

    public void setEventTime(String eventTime){this.eventTime = eventTime;}

    public void setEventPicData(byte[] picData){ //ALL = Data + File, rende disponibile sia la versione file che la versione binaria dell'immagine
        this.eventPicData = picData;
    }
    public void setEventOrganizer(String eventOrganizer){ //throws LengthFieldException {
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventName(){
        return this.eventName;
    }

    public String getEventCity(){
        return this.eventCity;
    }

    public String getEventAddress(){
        return this.eventAddress;
    }

    public String getEventMusicGenre(){
        return this.eventMusicGenre;
    }

    public String getEventDate(){
        return this.eventDate;
    }

    public String getEventTime(){
        return this.eventTime;
    }

    public String getEventOrganizer(){return this.eventOrganizer;}

    public byte[] getEventPicData(){return this.eventPicData;}

}
