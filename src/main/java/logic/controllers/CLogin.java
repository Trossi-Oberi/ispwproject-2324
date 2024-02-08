package logic.controllers;

import logic.dao.UserDAO;
import logic.model.MUser;
import logic.beans.BUserData;
import logic.model.Message;
import logic.server.NotificationServer;
import logic.utils.LoggedUser;
import logic.utils.GoogleLogin;
import logic.utils.MessageTypes;
import logic.utils.UserTypes;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CLogin {
    private UserDAO userDao;
    private MUser userModel;
    private boolean running;

    public CLogin() {
        this.userDao = new UserDAO();
        this.userModel = new MUser();
    }

    public int checkLoginControl(BUserData logBean, boolean isGoogleAuth, String authCode) throws RuntimeException{
        int ret = 0;
        if(!isGoogleAuth && authCode == null){
            //classic login
            this.userModel.setUsrAndPswByBean(logBean); //qui ancora non avviene il controllo della correttezza dei dati,
            ret = this.userDao.checkLoginInfo(this.userModel, false); //qui effettivamente e' il DAO che va a controllare la correttezza delle credenziali
        } else if (isGoogleAuth && authCode != null) {
            String userGoogleEmail;
            try {
                userGoogleEmail = GoogleLogin.getGoogleAccountEmail(GoogleLogin.getGoogleAccountCredentials(GoogleLogin.getGoogleAuthFlow(), authCode));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            logBean.setUsername(userGoogleEmail);
            this.userModel.setUsrAndPswByBean(logBean);
            ret = this.userDao.checkLoginInfo(this.userModel, true);
        }
        if (ret == 1) {
            createLoggedSession();
        }

        return ret;
    }

    private void createLoggedSession() {
        LoggedUser.setUserID(this.userModel.getUserID());
        LoggedUser.setUserName(this.userModel.getUserName());
        LoggedUser.setUserType(this.userModel.getUserType());
        LoggedUser.setFirstName(this.userModel.getFirstName());
        LoggedUser.setLastName(this.userModel.getLastName());
        LoggedUser.setGender(this.userModel.getGender());
        LoggedUser.setCity(this.userModel.getCity());
        LoggedUser.setBirthDate(this.userModel.getBirthDate());
    }

    public void closeLoginSession(){
        LoggedUser.setUserID(0);
        LoggedUser.setUserName(null);
        LoggedUser.setUserType(null);
        LoggedUser.setFirstName(null);
        LoggedUser.setLastName(null);
        LoggedUser.setGender(null);
        LoggedUser.setCity(null);
        LoggedUser.setBirthDate(null);

        //x Nicolas, aggiungere messaggio di disconnessione da inviare al server e settare stato Offline
    }

}
