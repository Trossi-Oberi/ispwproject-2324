package logic.controllers;

import logic.beans.*;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    //private GraphicControllerChat graphicChat;
    private CManageEvent addEventController;
    private BUserData dBean;

    public CFacade() {
    }

    //AddEvent methods
    public void addEvent(BEvent bean) {
        if (addEventController == null) {
            addEventController = new CManageEvent();
        }
        addEventController.addEvent(bean);
    }
}
