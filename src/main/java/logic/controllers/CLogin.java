package logic.controllers;

import logic.dao.UserDAO;
import logic.model.MUser;
import logic.beans.BUserData;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        this.userModel.setUsrAndPswByBean(logBean);
        ret = this.userDao.checkLogInInfo(this.userModel);
        if(ret == 1) {
            LoggedUser.setUserName(logBean.getUsername());
            //LoggedUser.setType(this.userModel.getUserType());
        }
        return ret;
    }

    /*
    public boolean insertNewUserControl(UserDataBean usrBean) throws DuplicateUsernameException {
        if(valideDateOfBirth(usrBean.getLocDateOfBirth()) == -1) {
            return false;
        }
        else {
            this.usrModel.setCredentialsByBean(usrBean);
            LoggedUser.setUserName(usrBean.getUsername());
            LoggedUser.setType(usrBean.getType());
            byte[] imm = new byte[(int)usrBean.getFileLength()];
            try {
                imm = Files.readAllBytes(usrBean.getFileImage().toPath());
            } catch (IOException e) {
                Logger.getLogger("WIG").log(Level.SEVERE, e.getMessage());
            }
            LoggedUser.setImage(imm);
            this.usrDao.insertNewUser(this.usrModel);
        }
        return true;
    }
     */

    private int valideDateOfBirth(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        // the user is for sure adult
        if(((today.getYear() - dateOfBirth.getYear()) > 18) || ((today.getYear() - dateOfBirth.getYear()) == 18 && dateOfBirth.getDayOfMonth() <= today.getDayOfMonth() && dateOfBirth.getMonthValue() <= today.getMonthValue())) {
            return 0;
        }
        return -1;
    }
}
