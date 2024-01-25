package logic.beans;

import java.time.LocalDate;
import java.util.logging.Logger;

public class BUserData {
    private String name;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private String typeOfUser;
    private String userName;
    private String psw;
    private Logger logger = Logger.getLogger("NightPlan");

    public BUserData() {

    }

    public BUserData(String username, String password) {
        this.userName = username;
        this.psw = password;
    }

    public BUserData(String username) {
        this.userName = username;
    }

    public void setName(String name){ //throws LengthFieldException, NullValueException {
//        if(name == null || name.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid name");
//        }
//        else if(name.length() > 20) {
//            throw new LengthFieldException("Too many character for name field");
//        }
        this.name = name;
    }

    public void setSurname(String surname){ //throws LengthFieldException, NullValueException{
//        if((surname == null || surname.equalsIgnoreCase(""))){
//            throw new NullValueException("Please insert a valid surname");
//        }
//        else if(surname.length() > 20) {
//            throw new LengthFieldException("Too many character for surname field");
//        }
        this.surname = surname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth){ //throws NullValueException {
//        if(dateOfBirth == null){
//            throw new NullValueException("Please insert a valid date of birth");
//        }
        this.dateOfBirth = dateOfBirth.toString();
    }

    public void setDateOfBirth(String dateOfBirth){// throws NullValueException {
//        if(dateOfBirth == null || dateOfBirth.equalsIgnoreCase("")){
//            throw new NullValueException("Please insert a valid date of birth");
//        }
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setType(String typeOfUsr) {
        this.typeOfUser = typeOfUsr;
    }

    public void setUserName(String usrName) {//throws LengthFieldException, NullValueException {
//        if(usrName == null || usrName.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid username");
//        }
//        else if(usrName.length() > 20) {
//            throw new LengthFieldException("Too many character for username field");
//        }
        this.userName = usrName;
    }

    public void setPsw(String passwd) {//throws LengthFieldException, NullValueException {
//        if(passwd == null || passwd.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid password");
//        }
//        else if(passwd.length() > 20) {
//            throw new LengthFieldException("Too many character for password field");
//        }
        this.psw = passwd;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public LocalDate getLocDateOfBirth() {
        return LocalDate.parse(this.dateOfBirth);
    }

    public String getGender() {
        return this.gender;
    }

    public String getType() {
        return this.typeOfUser;
    }

    public String getUsername() {
        return this.userName;
    }

    public String getPassword() {
        return this.psw;
    }
}
