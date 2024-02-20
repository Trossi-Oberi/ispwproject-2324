package logic.exceptions;

public class TextTooLongException extends Exception{
    private static final long serialVersionUID = 1L;
    private final String cause;

    public TextTooLongException(String message){
        this.cause = message;
    }

    @Override
    public String getMessage(){
        return this.cause;
    }
}
