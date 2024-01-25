package logic.utils;

public class LoggedUser {
    private static String username;
    private static UserTypes typeOfUser;

    private LoggedUser() {

    }

    public static void setUserName(String usrName) {
        username = usrName;
    }

    public static void setType(String type) {
        if (type.equals("User")) {
            typeOfUser = UserTypes.USER;
        }
        else if (type.equals("Organizer")) {
            typeOfUser = UserTypes.ORGANIZER;
        }
    }

    public static String getUserName() {
        return username;
    }

    public static UserTypes getUserType() {
        return typeOfUser;
    }
}
