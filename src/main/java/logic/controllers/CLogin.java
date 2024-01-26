package logic.controllers;

import logic.dao.UserDAO;
import logic.model.MUser;
import logic.beans.BUserData;
import logic.utils.LoggedUser;

public class CLogin {
    private UserDAO userDao;
    private MUser userModel;
    public CLogin() {
        this.userDao = new UserDAO();
        this.userModel = new MUser();
    }

    public int checkLogInControl(BUserData logBean) {
        int ret;
        this.userModel.setUsrAndPswByBean(logBean); //qui ancora non avviene il controllo della correttezza dei dati,
        ret = this.userDao.checkLoginInfo(this.userModel); //qui effettivamente e' il DAO che va a controllare la correttezza delle credenziali
        if(ret == 1) {
            LoggedUser.setUserName(logBean.getUsername());
            LoggedUser.setType(this.userModel.getUserType());
        }
        return ret;
    }
}
