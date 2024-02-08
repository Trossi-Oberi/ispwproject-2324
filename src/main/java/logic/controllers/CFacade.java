package logic.controllers;

import logic.beans.*;
import logic.exceptions.DuplicateEventParticipation;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent manageEventController;

    public CFacade() {
    }

    //AddEvent methods
    public boolean addEvent(BEvent bean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.addEvent(bean); //chiamata al controller effettivo
    }

    public boolean participateToEvent(BEvent bean) throws DuplicateEventParticipation {
        if(manageEventController == null){
            manageEventController = new CManageEvent();
        }

        return manageEventController.participateToEvent(bean); //chiamata al controller effettivo
    }

    public ArrayList<BEvent> retrieveEvents(UserTypes userType, String className){
        if (manageEventController == null){
            manageEventController = new CManageEvent();
        }
        return manageEventController.retrieveMyEvents(userType, className);
    }

    public int retrieveParticipationsToEvent(int id){
        if (manageEventController == null){
            manageEventController = new CManageEvent();
        }
        return manageEventController.getParticipationsToEvent(id);
    }

    public int loginUser(BUserData bean){
        if (loginController == null){
            loginController = new CLogin();
        }
        return loginController.checkLoginControl(bean);
    }

    public int initGoogleAuth() throws RuntimeException{
        if (loginController == null){
            loginController = new CLogin();
        }
        return loginController.initGoogleAuth();
    }

    public int googleLoginUser(BUserData bean, String code){
        if (loginController == null){
            loginController = new CLogin();
        }
        return loginController.checkGoogleLoginControl(bean, code);
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
