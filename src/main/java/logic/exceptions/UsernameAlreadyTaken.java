package logic.exceptions;

/*Matteo Trossi*/

public class UsernameAlreadyTaken extends Exception {
    private static final long serialVersionUID = 1L;

    private String username;

    public UsernameAlreadyTaken(String message){
        super(message);
    }

    public UsernameAlreadyTaken(String message, String username){
        this(message);
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
