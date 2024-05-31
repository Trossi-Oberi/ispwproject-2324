package logic.controllers;

import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.exceptions.MinimumAgeException;
import logic.exceptions.UsernameAlreadyTaken;
import logic.model.MUser;
import logic.dao.LocationDAO;

import java.time.LocalDate;
import java.util.List;


public class CRegistration {
    private LocationDAO locationDao;
    private UserDAO userDao;
    private MUser userModel;

    public CRegistration() {
        this.userDao = new UserDAO();
        this.userModel = new MUser();
        this.locationDao = new LocationDAO();
    }

    public boolean registerUserControl(BUserData usrBean) throws UsernameAlreadyTaken, MinimumAgeException {
        if (checkBirthDate(usrBean.getBirthDate()) == -1) {
            throw new MinimumAgeException("Minimum age requirement not reached");
        } else {
            this.userModel.setCredentialsByBean(usrBean);
            this.userDao.registerUser(this.userModel);
            this.userModel.setId(userDao.getUserIDByUsername(this.userModel.getUserName()));
            usrBean.setUserID(userDao.getUserIDByUsername(this.userModel.getUserName()));
        }
        return true;
    }

    public List<String> getProvincesList() {
        List<String> provincesList;
        provincesList = this.locationDao.getProvincesList();
        return provincesList;
    }

    public List<String> getCitiesList(String selectedProvince) {
        List<String> citiesList;
        citiesList = this.locationDao.getCitiesList(selectedProvince);
        return citiesList;
    }

    private int checkBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        // the user is for sure adult
        if (((today.getYear() - birthDate.getYear()) > 18) || ((today.getYear() - birthDate.getYear()) == 18 && birthDate.getDayOfMonth() <= today.getDayOfMonth() && birthDate.getMonthValue() <= today.getMonthValue())) {
            return 0;
        }
        return -1;
    }
}
