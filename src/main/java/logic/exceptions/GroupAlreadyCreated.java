package logic.exceptions;

public class GroupAlreadyCreated extends Exception{
    private static final long serialVersionUID = 1L;

    private Integer eventID;

    public GroupAlreadyCreated(String message){
        super(message);
    }

    public GroupAlreadyCreated(String message, Integer eventID){
        this(message);
        this.eventID = eventID;
    }

    public Integer getEventID(){
        return this.eventID;
    }


}
