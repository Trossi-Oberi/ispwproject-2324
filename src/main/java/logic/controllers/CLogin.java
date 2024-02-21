package logic.controllers;

import logic.dao.UserDAO;
import logic.exceptions.InvalidTokenValue;
import logic.model.MUser;
import logic.beans.BUserData;
import logic.server.Server;
import logic.utils.LoggedUser;
import logic.utils.GoogleLogin;
import logic.utils.SecureObjectInputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import static logic.view.EssentialGUI.logger;

public class CLogin {
    private UserDAO userDao;
    private MUser userModel;

    public CLogin() {
        this.userDao = new UserDAO();
        this.userModel = new MUser();
    }

    public int checkLoginControl(BUserData logBean, boolean isGoogleAuth, String authCode) throws InvalidTokenValue{
        int ret = 0;
        if (!isGoogleAuth && authCode == null) {
            //classic login
            this.userModel.setUsrAndPswByBean(logBean); //qui ancora non avviene il controllo della correttezza dei dati,
            ret = this.userDao.checkLoginInfo(this.userModel, false); //qui effettivamente è il DAO che va a controllare la correttezza delle credenziali
        } else if (isGoogleAuth && authCode != null) {
            String userGoogleEmail;
            try {
                userGoogleEmail = GoogleLogin.getGoogleAccountEmail(Objects.requireNonNull(GoogleLogin.getGoogleAccountCredentials(GoogleLogin.getGoogleAuthFlow(), authCode)));
                logBean.setUsername(userGoogleEmail);
            } catch (InvalidTokenValue e) {
                //gestione token invalido
                throw e;
            } catch (Exception e) {
                //grave errore perché non dovrebbe mai andare qui, se ci va è grave!!!!
                logger.severe(e.getMessage());
            }
            this.userModel.setUsrAndPswByBean(logBean);
            ret = this.userDao.checkLoginInfo(this.userModel, true);
        }
        if (ret == 1) {
            createLoggedSession();
        }

        return ret;
    }

    public int changeCity(int userID, String province, String city) {
        return this.userDao.changeCity(userID, province, city);
    }

    public String getUsernameByID(int userID) {
        return this.userDao.getUsernameByID(userID);
    }

    private void createLoggedSession() {
        ObjectOutputStream out = null;
        SecureObjectInputStream in = null;
        Socket client = null;
        try {
            client = new Socket(Server.ADDRESS, Server.PORT);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new SecureObjectInputStream(client.getInputStream());
        } catch (UnknownHostException e) {
            logger.severe(e.getMessage());
        } catch (IOException e) {
            logger.severe("Logger"+e.getMessage());
        }
        LoggedUser.setSocket(client);
        LoggedUser.setOutputStream(out);
        LoggedUser.setInputStream(in);
        LoggedUser.setUserID(this.userModel.getUserID());
        LoggedUser.setUserName(this.userModel.getUserName());
        LoggedUser.setUserType(this.userModel.getUserType());
        LoggedUser.setFirstName(this.userModel.getFirstName());
        LoggedUser.setLastName(this.userModel.getLastName());
        LoggedUser.setGender(this.userModel.getGender());
        LoggedUser.setProvince(this.userModel.getProvince());
        LoggedUser.setCity(this.userModel.getCity());
        LoggedUser.setBirthDate(this.userModel.getBirthDate());
        LoggedUser.setStatus("Online");
        //set status gestita dal UserDAO
        this.userDao.setStatus(this.userModel.getUserID());
    }

    public void closeLoginSession() {
        LoggedUser.setUserID(0);
        LoggedUser.setUserName(null);
        LoggedUser.setUserType(null);
        LoggedUser.setFirstName(null);
        LoggedUser.setLastName(null);
        LoggedUser.setGender(null);
        LoggedUser.setProvince(null);
        LoggedUser.setCity(null);
        LoggedUser.setBirthDate(null);
        LoggedUser.setStatus("Offline");
        //set status gestita dal UserDAO
        this.userDao.setStatus(LoggedUser.getUserID());

        //Chiusura socket
        try {
            LoggedUser.getOutputStream().close();
            LoggedUser.getInputStream().close();
        } catch (IOException e){
            logger.severe("Error while closing logged user socket streams: " + e.getMessage());
        }
    }

}
