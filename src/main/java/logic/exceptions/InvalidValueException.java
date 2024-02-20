package logic.exceptions;

public class InvalidValueException extends Exception{
    private static final long serialVersionUID = 2188732812891289L;

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause){
        super(message + cause.getMessage());
    }
}
