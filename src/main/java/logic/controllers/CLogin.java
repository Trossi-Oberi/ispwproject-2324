package logic.controllers;

import logic.dao.UserDAO;
import logic.model.MUser;
import logic.beans.BUserData;
import logic.utils.LoggedUser;

import logic.utils.GoogleLogin;

public class CLogin {
    private UserDAO userDao;
    private MUser userModel;

    public CLogin() {
        this.userDao = new UserDAO();
        this.userModel = new MUser();
    }

    public int checkLoginControl(BUserData logBean) {
        int ret;
        this.userModel.setUsrAndPswByBean(logBean); //qui ancora non avviene il controllo della correttezza dei dati,
        ret = this.userDao.checkLoginInfo(this.userModel, false); //qui effettivamente e' il DAO che va a controllare la correttezza delle credenziali
        if (ret == 1) {
            createLoggedSession();
        }
        return ret;
    }

    public int initGoogleAuth() throws RuntimeException{
        int ret = 0;
        try {
            if(GoogleLogin.initGoogleLogin() == 1){
                ret = 1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public int checkGoogleLoginControl(BUserData googleLogBean, String authCode) {
        int ret = 0;
        String userGoogleEmail;
        try {
            userGoogleEmail = GoogleLogin.getGoogleAccountEmail(GoogleLogin.getGoogleAccountCredentials(GoogleLogin.getGoogleAuthFlow(), authCode));
            googleLogBean.setUsername(userGoogleEmail);
            this.userModel.setUsrAndPswByBean(googleLogBean);
            ret = this.userDao.checkLoginInfo(this.userModel, true);
            if (ret == 1) {
                createLoggedSession();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
}
