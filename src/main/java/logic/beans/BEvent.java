package logic.beans;

public class BEvent {
    protected String eventName;
    protected String eventProvince;
    protected String eventCity;
    protected String eventAddress;
    protected String eventMusicGenre;
    protected String eventDate;
    protected String eventTime;
    private int eventID;
    private byte[] eventPicData;
    private String eventOrganizer;
    private int eventOrganizerID;
    private String eventPicPath;

    public BEvent() {
        //empty
    }

    //SETTERS
    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setEventOrganizer(String eventOrganizer) { //throws LengthFieldException {
        this.eventOrganizer = eventOrganizer;
    }

    public void setEventOrganizerID(int eventOrgID) {
        this.eventOrganizerID = eventOrgID;
    }

    public void setEventName(String eventName) { //throws LengthFieldException, NullValueException{
        this.eventName = eventName;
    }

    public void setEventProvince(String province) {
        this.eventProvince = province;
    }

    public void setEventCity(String eventCity) { //throws LengthFieldException, NullValueException{
        this.eventCity = eventCity;
    }

    public void setEventAddress(String eventAddress) { //throws LengthFieldException, NullValueException{
        this.eventAddress = eventAddress;
    }

    public void setEventMusicGenre(String eventMusicGenre) { //throws LengthFieldException, NullValueException{
        this.eventMusicGenre = eventMusicGenre;
    }

    public void setEventDate(String eventDate) { //throws LengthFieldException, NullValueException{
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventHour, String eventMinutes) { //throws LengthFieldException, NullValueException{
        this.eventTime = eventHour + ":" + eventMinutes;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventPicData(byte[] picData) { //ALL = Data + File, rende disponibile sia la versione file che la versione binaria dell'immagine
        this.eventPicData = picData;
    }

    public void setEventPicPath(String path) {
        this.eventPicPath = path;
    }

    //GETTERS
    public int getEventID() {
        return this.eventID;
    }

    public String getEventOrganizer() {
        return this.eventOrganizer;
    }

    public int getEventOrganizerID() {
        return this.eventOrganizerID;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getEventProvince() {
        return this.eventProvince;
    }

    public String getEventCity() {
        return this.eventCity;
    }

    public String getEventAddress() {
        return this.eventAddress;
    }

    public String getEventMusicGenre() {
        return this.eventMusicGenre;
    }

    public String getEventDate() {
        return this.eventDate;
    }

    public String getEventTime() {
        return this.eventTime;
    }

    public byte[] getEventPicData() {
        return this.eventPicData;
    }

    public String getEventPicPath() {
        return this.eventPicPath;
    }
}
