package logic.model;

import java.time.LocalDate;

import logic.beans.BUserData;

public class MUser {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;
    private String username;
    private String password;
    private String userType;

    public void setCredentialsByBean(BUserData dataBean) {
        this.firstName = dataBean.getFirstName();
        this.lastName = dataBean.getLastName();
        this.birthDate = dataBean.getBirthDate().toString();
        this.gender = dataBean.getGender();
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
        this.userType = dataBean.getUserType();
    }

    public void setUsrAndPswByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }
    public void setBirthDate(String birthdate) {
        this.birthDate = birthdate;
    }
    public void setGender(String gen) {
        this.gender = gen;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserType() {
        return this.userType;
    }
}
