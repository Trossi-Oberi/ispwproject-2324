package logic.beans;

import logic.utils.UserTypes;

import java.time.LocalDate;

public class BUserData {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String city;
    private UserTypes userType;
    private String username;
    private String password;

    public BUserData() {

    }

    public BUserData(String username) {
        this.username = username;
    }

    public BUserData(String username, String password) {
        this(username);
        this.password = password;
    }

    public void setFirstName(String firstName) { //throws LengthFieldException, NullValueException {
//        if(name == null || name.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid name");
//        }
//        else if(name.length() > 20) {
//            throw new LengthFieldException("Too many character for name field");
//        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) { //throws LengthFieldException, NullValueException{
//        if((surname == null || surname.equalsIgnoreCase(""))){
//            throw new NullValueException("Please insert a valid surname");
//        }
//        else if(surname.length() > 20) {
//            throw new LengthFieldException("Too many character for surname field");
//        }
        this.lastName = lastName;
    }

    public void setBirthDate(LocalDate birthDate) { //throws NullValueException {
//        if(dateOfBirth == null){
//            throw new NullValueException("Please insert a valid date of birth");
//        }
        this.birthDate = LocalDate.parse(birthDate.toString());
    }

    public void setCity(String city) {
        this.city = city;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setType(UserTypes typeOfUsr) {
        this.userType = typeOfUsr;
    }

    public void setUsername(String usrName) {//throws LengthFieldException, NullValueException {
//        if(usrName == null || usrName.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid username");
//        }
//        else if(usrName.length() > 20) {
//            throw new LengthFieldException("Too many character for username field");
//        }
        this.username = usrName;
    }

    public void setPassword(String passwd) {//throws LengthFieldException, NullValueException {
//        if(passwd == null || passwd.equalsIgnoreCase("")) {
//            throw new NullValueException("Please insert a valid password");
//        }
//        else if(passwd.length() > 20) {
//            throw new LengthFieldException("Too many character for password field");
//        }
        this.password = passwd;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public UserTypes getUserType() {
        return this.userType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCity() {
        return this.city;
    }
}
