package logic.controllers;

import logic.beans.*;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent manageEventController;
    private BUserData dBean;

    public CFacade() {
    }

    //AddEvent methods
    public boolean addEvent(BEvent bean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.addEvent(bean); //chiamata al controller effettivo
    }

    public ArrayList<BEvent> retrieveEvents(UserTypes userType, String fxmlpage){
        if (manageEventController == null){
            manageEventController = new CManageEvent();
        }
        return manageEventController.retrieveMyEvents(userType,fxmlpage);
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
