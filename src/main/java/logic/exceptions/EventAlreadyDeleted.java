package logic.exceptions;

public class EventAlreadyDeleted extends Exception {
    private static final long serialVersionUID = 1L;

    public EventAlreadyDeleted(String message){
        super(message);
    }
}
