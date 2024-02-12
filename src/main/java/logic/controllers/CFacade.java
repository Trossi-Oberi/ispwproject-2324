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
        if(res) {
            if (notificationController == null){
                notificationController = new CNotification();
            }
            notificationController.sendAddEventMessage(bean.getEventOrganizerID(), bean.getEventID(), bean.getEventCity());
        }
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

    public boolean registerUser(BUserData bean) throws RuntimeException {
        if (regController == null){
            regController = new CRegistration();
        }
        boolean res = regController.registerUserControl(bean);

        //se completo con successo la registrazione di un nuovo utente allora effettuo una connessione al server
        if (res) {
            if(bean.getUserType().equals(UserTypes.USER)){
                if (notificationController == null) {
                    notificationController = new CNotification(); //inizializzo il controller delle notifiche
                }
                notificationController.sendRegMessage(bean.getUserID(), bean.getCity());
            }
        }
        return res;
    }

    public int loginUser(BUserData bean, boolean isGoogleAuth, String authCode) throws RuntimeException{
        if (loginController == null) {
            loginController = new CLogin();
        }
        int loginRes = loginController.checkLoginControl(bean, isGoogleAuth, authCode);
        if(loginRes == 1) {
            if (notificationController == null) {
                notificationController = new CNotification();
            }
            notificationController.sendLoginMsg(LoggedUser.getUserID(), LoggedUser.getCity());
        }
        return loginRes;
    }

    public void signOut(){
        //effettuo la disconnessione dal server
        if (notificationController == null){
            notificationController = new CNotification();
        }
        if(notificationController.sendLogoutMsg(LoggedUser.getUserID())){
            //dopo la disconnessione dal server chiudo la sessione di Login
            if (loginController == null) {
                loginController = new CLogin();
            }
            loginController.closeLoginSession();
        }
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
