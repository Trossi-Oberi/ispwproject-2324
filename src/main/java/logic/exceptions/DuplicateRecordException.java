package logic.exceptions;

public class DuplicateRecordException extends Exception {

    private static final long serialVersionUID = 9128379187L; //serve per la serializzazione

    public DuplicateRecordException(String message) {
        super(message);
    }
}
