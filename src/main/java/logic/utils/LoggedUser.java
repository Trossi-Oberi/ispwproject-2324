package logic.utils;


import logic.utils.enums.UserTypes;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoggedUser {
    private static String username;
    private static UserTypes userType;
    private static String firstName;
    private static String lastName;
    private static String birthDate;
    private static String gender;
    private static String city;
    private static String province;
    private static String status;
    private static int userID;
    private static SecureObjectInputStream in;
    private static ObjectOutputStream out;
    private static Socket client;

    private LoggedUser() {
        //empty
    }

    //SETTERS
    public static void setUserName(String usrName) {
        username = usrName;
    }

    public static void setUserType(UserTypes type) {
        userType = type;
    }

    public static void setFirstName(String firstname) {
        firstName = firstname;
    }

    public static void setLastName(String lastname) {
        lastName = lastname;
    }

    public static void setBirthDate(String birthdate) {
        birthDate = birthdate;
    }

    public static void setGender(String gen) {
        gender = gen;
    }

    public static void setProvince(String prov) {
        province = prov;
    }

    public static void setCity(String cit) {
        city = cit;
    }

    public static void setStatus(String stat) {
        status = stat;
    }

    public static void setUserID(int id) {
        userID = id;
    }

    public static void setOutputStream(ObjectOutputStream output) {
        out = output;
    }

    public static void setInputStream(SecureObjectInputStream input) {
        in = input;
    }
    public static void setSocket(Socket clientSocket) {
        client = clientSocket;
    }

    //GETTERS

    public static String getUserName() {
        return username;
    }

    public static UserTypes getUserType() {
        return userType;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getBirthDate() {
        return birthDate;
    }

    public static String getGender() {
        return gender;
    }

    public static String getProvince() {
        return province;
    }

    public static String getCity() {
        return city;
    }

    public static int getUserID() {
        return userID;
    }

    public static String getStatus() {
        return status;
    }

    public static ObjectOutputStream getOutputStream() {
        return out;
    }

    public static SecureObjectInputStream getInputStream() {
        return in;
    }

    public static Socket getSocket() {
        return client;
    }
}
