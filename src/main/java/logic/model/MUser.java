package logic.model;

import java.time.LocalDate;

import logic.beans.BUserData;

public class MUser {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String username;
    private String password;
    private String userType;

    public void setCredentialsByBean(BUserData dataBean) {
        this.firstName = dataBean.getName();
        this.lastName = dataBean.getSurname();
        this.birthDate = dataBean.getLocDateOfBirth();
        this.gender = dataBean.getGender();
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
        this.userType = dataBean.getType();
    }

    public void setUsrAndPswByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
    }
    public void setUserType(String userType) {
        this.userType = userType;
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
