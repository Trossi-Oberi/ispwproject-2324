package logic.controllers;

import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MUser;

import java.time.LocalDate;


public class CRegistration {
    private UserDAO userDao;
    private MUser userModel;

    public CRegistration(){
        this.userDao = new UserDAO();
        this.userModel = new MUser();
    }

    public boolean registerUserControl(BUserData usrBean) {
        if(checkBirthDate(usrBean.getBirthDate()) == -1) {
            return false;
        }
        else {
            this.userModel.setCredentialsByBean(usrBean);
            this.userDao.registerUser(this.userModel);
        }
        return true;
    }

    private int checkBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        // the user is for sure adult
        if(((today.getYear() - birthDate.getYear()) > 18) || ((today.getYear() - birthDate.getYear()) == 18 && birthDate.getDayOfMonth() <= today.getDayOfMonth() && birthDate.getMonthValue() <= today.getMonthValue())) {
            return 0;
        }
        return -1;
    }

}
