package logic.controllers;

import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MUser;
import logic.dao.LocationDAO;

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

    public boolean fetchProvincesList(ArrayList<String> provincesList){
        if(this.locationDao.getProvincesList(provincesList)){
            return true;
        } else {
            return false;
        }

    }

    public boolean fetchCitiesList(ArrayList<String> citiesList, String selectedProvince){
        if(this.locationDao.getCitiesList(citiesList, selectedProvince)){
            return true;
        } else {
            return false;
        }

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
