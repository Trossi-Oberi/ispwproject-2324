package logic.exceptions;

public class DuplicateEventParticipation extends Exception{
    private static final long serialVersionUID = 1L; //serve per la serializzazione

    public DuplicateEventParticipation(String message) {
        super(message);
    }
}
