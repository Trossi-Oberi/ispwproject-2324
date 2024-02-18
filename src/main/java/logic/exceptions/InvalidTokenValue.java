package logic.exceptions;

public class InvalidTokenValue extends Exception{
    private static final long serialVersionUID = 2188732812891289L;

    public InvalidTokenValue(String message) {
        super(message);
    }

    public InvalidTokenValue(String message, Throwable cause){
        super(message + cause.getMessage());
    }


}
