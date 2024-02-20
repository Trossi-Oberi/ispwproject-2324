package logic.exceptions;

/*Matteo Trossi*/

public class EventAlreadyAdded extends Exception {
    private static final long serialVersionUID = 1L;

    private final String eventName;

   /* public EventAlreadyAdded(String message){
        super(message);
    }*/

    public EventAlreadyAdded(String message, String eventName){
        super(message);
        this.eventName = eventName;
    }

    public String getEventName(){
        return this.eventName;
    }
}
