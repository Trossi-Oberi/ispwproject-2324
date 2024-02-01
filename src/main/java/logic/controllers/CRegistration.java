package logic.controllers;

import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MUser;
import logic.dao.LocationDAO;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;


public class CRegistration {
    private LocationDAO locationDao;
    private UserDAO userDao;
    private MUser userModel;

    public CRegistration(){
        this.userDao = new UserDAO();
        this.userModel = new MUser();
        this.locationDao = new LocationDAO();
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

    public ArrayList<String> getProvincesList(){
        ArrayList<String> provincesList;
        provincesList = this.locationDao.getProvincesList();
        return provincesList;
    }

    public ArrayList<String> getCitiesList(String selectedProvince){
        ArrayList<String> citiesList;
        citiesList = this.locationDao.getCitiesList(selectedProvince);
        return citiesList;
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
