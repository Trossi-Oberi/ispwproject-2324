package logic.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.beans.BUserData;

public class MUser {
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String gender;
    private String username;
    private String password;
    private String userType;

    public void setCredentialsByBean(BUserData dataBean) {
        this.name = dataBean.getName();
        this.surname = dataBean.getSurname();
        this.dateOfBirth = dataBean.getLocDateOfBirth();
        this.gender = dataBean.getGender();
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
        this.userType = dataBean.getType();
    }

    public void setUsrAndPswByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
    }

    public void setUsrNameByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
    }

    public void setUsrByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
    }

    public void setLogUsrCred(String typeOfUsr) {
        this.userType = typeOfUsr;
    }

    public void setUsrName(String usrName) {
        this.username = usrName;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getUserName() {
        return this.username;
    }

    public String getUserType() {
        return this.userType;
    }

    public String getPassword() {
        return this.password;
    }
}
