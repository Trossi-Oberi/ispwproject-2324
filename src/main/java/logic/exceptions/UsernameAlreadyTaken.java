package logic.exceptions;

/*Matteo Trossi*/

public class UsernameAlreadyTaken extends Exception {
    private static final long serialVersionUID = 1L;

    private final String username;

    public UsernameAlreadyTaken(String message, String username){
        super(message);
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
