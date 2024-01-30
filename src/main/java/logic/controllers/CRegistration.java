package logic.controllers;

import javafx.scene.control.ChoiceBox;
import logic.beans.BCity;
import logic.beans.BProvince;
import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MCity;
import logic.model.MProvince;
import logic.model.MUser;

import java.time.LocalDate;

public class CRegistration {

    private UserDAO userDao;
    private MUser userModel;

    private MCity cityModel;

    private MProvince provinceModel;

    public CRegistration(){
        this.userDao = new UserDAO();
        this.userModel = new MUser();
        this.cityModel = new MCity();
        this.provinceModel = new MProvince();
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

    public void retrieveProvinces(BProvince provinceBean, ChoiceBox<String> provinceBox){
            this.provinceModel.getProvinces().thenAccept(provinces -> {
                provinceBean.setProvincesList(this.provinceModel.getProvincesList());
                for (int i = 0; i < provinceBean.getProvincesList().size(); i++) {
                    System.out.println(provinceBean.getProvincesList().get(i));
                }
                provinceBox.getItems().addAll(provinceBean.getProvincesList());
            });
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
