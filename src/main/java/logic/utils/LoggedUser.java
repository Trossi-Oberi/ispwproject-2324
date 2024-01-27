package logic.utils;

public class LoggedUser {
    private static String username;
    private static UserTypes userType;

    private LoggedUser() {

    }

    public static void setUserName(String usrName) {
        username = usrName;
    }

    public static void setUserType(String type) {
        if (type.equals("User")) {
            userType = UserTypes.USER;
        }
        else if (type.equals("Organizer")) {
            userType = UserTypes.ORGANIZER;
        }
    }

    public static String getUserName() {
        return username;
    }

    public static UserTypes getType() {
        return userType;
    }
}
