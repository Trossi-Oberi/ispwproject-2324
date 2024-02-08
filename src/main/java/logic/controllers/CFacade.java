package logic.controllers;

import logic.beans.*;
import logic.exceptions.DuplicateEventParticipation;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent manageEventController;
    private CNotification notificationController;

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

    public int loginUser(BUserData bean, boolean isGoogleAuth, String authCode) throws RuntimeException{
        if (loginController == null){
            loginController = new CLogin();
        }
        int loginRes = loginController.checkLoginControl(bean, isGoogleAuth, authCode);
        if (notificationController == null){
            notificationController = new CNotification();
        }
        notificationController.connectToNotificationServer(LoggedUser.getUserID());
        return loginRes;
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

    public void signOut(){
        if (loginController == null) {
            loginController = new CLogin();
        }
        loginController.closeLoginSession();
    }


}
