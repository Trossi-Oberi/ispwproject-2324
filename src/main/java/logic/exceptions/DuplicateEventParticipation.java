package logic.exceptions;

import java.io.Serial;

public class DuplicateEventParticipation extends Exception{
    @Serial
    private static final long serialVersionUID = 1L; //serve per la serializzazione

    public DuplicateEventParticipation(String message) {
        super(message);
    }
}
