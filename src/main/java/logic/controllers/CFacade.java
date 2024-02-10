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
        boolean res = manageEventController.addEvent(bean); //chiamata al controller effettivo
//        if (notificationController == null){
//            notificationController = new CNotification();
//        }
        notificationController.sendAddEventMessage(bean.getEventOrganizerID(), bean.getEventID(), bean.getEventCity());
        return res;
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
        if (loginController == null) {
            loginController = new CLogin();
        }
        int loginRes = loginController.checkLoginControl(bean, isGoogleAuth, authCode);
        if (notificationController == null) {
            notificationController = new CNotification();
        }
        notificationController.connectToNotificationServer(LoggedUser.getUserID());
        return loginRes;
    }

    public boolean registerUser(BUserData bean){
        if (regController == null){
            regController = new CRegistration();
        }
        boolean res = regController.registerUserControl(bean);
        if(res){
            if (bean.getUserType()==UserTypes.USER){
                if (notificationController == null){
                    notificationController = new CNotification();
                }
                notificationController.sendRegMessage(bean.getUserID(), bean.getCity());
                notificationController.connectToNotificationServer(bean.getUserID());
                notificationController.disconnectFromServer(bean.getUserID());
            }
        }
        return res;

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
        if (notificationController == null){
            notificationController = new CNotification();
        }
        notificationController.disconnectFromServer(LoggedUser.getUserID());

        if (loginController == null) {
            loginController = new CLogin();
        }
        loginController.closeLoginSession();
    }


}
