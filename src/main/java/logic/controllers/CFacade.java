package logic.controllers;

import logic.beans.*;

import java.util.ArrayList;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent addEventController;
    private BUserData dBean;

    public CFacade() {
    }

    //AddEvent methods
    public boolean addEvent(BEvent bean) {
        if (addEventController == null) {
            addEventController = new CManageEvent();
        }
        return addEventController.addEvent(bean); //chiamata al controller effettivo
    }

    public int loginUser(BUserData bean){
        if (loginController == null){
            loginController = new CLogin();
        }
        return loginController.checkLogInControl(bean);
    }

    public boolean registerUser(BUserData bean){
        if (regController == null){
            regController = new CRegistration();
        }
        return regController.registerUserControl(bean);
    }

    public ArrayList<String> getProvincesList(){
        if (regController == null){
            regController = new CRegistration();
        }
        return regController.getProvincesList();
    }

    public ArrayList<String> getCitiesList(String province){
        if (regController == null){
            regController = new CRegistration();
        }
        return regController.getCitiesList(province);
    }
}
