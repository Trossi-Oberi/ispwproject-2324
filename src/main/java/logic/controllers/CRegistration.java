package logic.controllers;

import logic.beans.BCity;
import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MCity;
import logic.model.MUser;

import java.time.LocalDate;


public class CRegistration {

    private UserDAO userDao;
    private MUser userModel;

    private MCity cityModel;

    public CRegistration(){
        this.userDao = new UserDAO();
        this.userModel = new MUser();
        this.cityModel = new MCity();
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

    public boolean retrieveCities(BCity cityBean){
        this.cityModel.setField(cityBean.getField());
        this.cityModel.getCitiesByProvince(this.cityModel.getField());
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
