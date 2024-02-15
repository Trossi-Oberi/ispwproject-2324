package logic.controllers;

import logic.beans.*;
import logic.exceptions.DuplicateEventParticipation;
import logic.model.Message;
import logic.utils.LoggedUser;
import logic.utils.NotificationTypes;
import logic.utils.UserTypes;

import java.util.ArrayList;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent manageEventController;
    private CNotification notificationController;

    public CFacade() {
    }

    //Metodi che interagiscono col server inviando notifiche
    public boolean addEvent(BEvent bean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.addEvent(bean); //chiamata al controller effettivo
        if (res) {
            if (notificationController == null) {
                notificationController = new CNotification();
            }
            notificationController.sendMessage(NotificationTypes.EventAdded, bean.getEventOrganizerID(), bean.getEventID(), bean.getEventCity(), null);
        }
        return res;
    }

    public boolean deleteEvent(int eventID){
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.deleteEvent(eventID);
        if (res){
            if (notificationController == null) {
                notificationController = new CNotification();
            }
            notificationController.sendMessage(NotificationTypes.EventDeleted, null, eventID, null, null);
        }
        return res;
    }

    public boolean participateToEvent(BEvent bean) throws DuplicateEventParticipation {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.participateToEvent(bean);
        if(res){
            if(notificationController == null){
                notificationController = new CNotification();
            }
            notificationController.sendMessage(NotificationTypes.UserEventParticipation, LoggedUser.getUserID(), bean.getEventID(), null, null);
        }
        return res;
    }

    public boolean registerUser(BUserData bean) throws RuntimeException {
        if (regController == null) {
            regController = new CRegistration();
        }
        boolean res = regController.registerUserControl(bean);

        //se completo con successo la registrazione di un nuovo utente allora effettuo una connessione al server
        if (res) {
            if (bean.getUserType().equals(UserTypes.USER)) {
                if (notificationController == null) {
                    notificationController = new CNotification(); //inizializzo il controller delle notifiche
                }
                notificationController.sendMessage(NotificationTypes.UserRegistration, bean.getUserID(), null, bean.getCity(), null); //null perche' e' ovvio sia UserType user
            }
        }
        return res;
    }

    public int loginUser(BUserData bean, boolean isGoogleAuth, String authCode) throws RuntimeException {
        if (loginController == null) {
            loginController = new CLogin();
        }
        int loginRes = loginController.checkLoginControl(bean, isGoogleAuth, authCode);
        if (loginRes == 1) {
            if (notificationController == null) {
                notificationController = new CNotification();
            }
            notificationController.sendMessage(NotificationTypes.LoggedIn, LoggedUser.getUserID(), null, LoggedUser.getCity(), LoggedUser.getUserType());
        }
        return loginRes;
    }

    public void signOut() {
        //effettuo la disconnessione dal server
        if (notificationController == null) {
            notificationController = new CNotification();
        }
        notificationController.sendMessage(NotificationTypes.Disconnected, LoggedUser.getUserID(), null, null, LoggedUser.getUserType());
        //dopo la disconnessione dal server chiudo la sessione di Login
        if (loginController == null) {
            loginController = new CLogin();
        }
        loginController.closeLoginSession();
    }


    //Metodi che non interagiscono col server
    public ArrayList<BEvent> retrieveEvents(UserTypes userType, String className) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.retrieveMyEvents(userType, className);
    }

    public boolean editEvent(BEvent eventBean){
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.editEvent(eventBean);
    }



    public int retrieveParticipationsToEvent(int id) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.getParticipationsToEvent(id);
    }


    public ArrayList<String> getProvincesList() {
        if (regController == null) {
            regController = new CRegistration();
        }
        return regController.getProvincesList();
    }

    public ArrayList<String> getCitiesList(String province) {
        if (regController == null) {
            regController = new CRegistration();
        }
        return regController.getCitiesList(province);
    }


    public ArrayList<BMessage> retrieveNotifications(int userID) {
        if(notificationController == null){
            notificationController = new CNotification();
        }
        return notificationController.retrieveNotifications(userID);
    }

    public String getEventNameByEventID(int eventID) {
        if(manageEventController == null){
            manageEventController = new CManageEvent();
        }
        return manageEventController.getEventNameByEventID(eventID);
    }

    public int changeUserCity(int userID, String province, String city) {
        if(loginController == null){
            loginController = new CLogin();
        }
        return loginController.changeCity(userID, province, city);
    }

    public String getUsernameByID(int userID){
        if(loginController == null){
            loginController = new CLogin();
        }
        return loginController.getUsernameByID(userID);
    }
}
