package logic.utils;


public class LoggedUser {
    private static String username;
    private static UserTypes userType;

    private static String firstName;
    private static String lastName;
    private static String birthDate;
    private static String gender;

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

    public static void setFirstName(String firstname){
        firstName = firstname;
    }

    public static void setLastName(String lastname){
        lastName = lastname;
    }

    public static void setBirthDate(String birthdate){
        birthDate = birthdate;
    }

    public static void setGender(String gen){
        gender = gen;
    }

    public static String getUserName() {
        return username;
    }

    public static UserTypes getUserType() {
        return userType;
    }

    public static String getFirstName(){
        return firstName;
    }

    public static String getLastName(){
        return lastName;
    }

    public static String getBirthDate(){
        return birthDate;
    }

    public static String getGender(){
        return gender;
    }
}
