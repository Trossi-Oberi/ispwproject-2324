package logic.controllers;

import logic.beans.*;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent addEventController;
    private BUserData dBean;

    public CFacade() {
    }

    //AddEvent methods
    public void addEvent(BEvent bean) {
        if (addEventController == null) {
            addEventController = new CManageEvent();
        }
        addEventController.addEvent(bean); //chiamata al controller effettivo
    }

    public void checkLogIn(BUserData bean){
        if (loginController == null){
            loginController = new CLogin();
        }
        loginController.checkLogInControl(bean);
    }

    public boolean registerUser(BUserData bean){
        if (regController == null){
            regController = new CRegistration();
        }
        return regController.registerUserControl(bean);
    }
}
