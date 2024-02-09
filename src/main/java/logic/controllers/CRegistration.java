package logic.controllers;

import logic.beans.BUserData;
import logic.dao.UserDAO;
import logic.model.MUser;
import logic.dao.LocationDAO;
import logic.model.Message;
import logic.server.Server;
import logic.utils.MessageTypes;
import logic.utils.UserTypes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;


public class CRegistration {
    private LocationDAO locationDao;
    private UserDAO userDao;
    private MUser userModel;

    public CRegistration(){
        this.userDao = new UserDAO();
        this.userModel = new MUser();
        this.locationDao = new LocationDAO();
    }

    public boolean registerUserControl(BUserData usrBean) {
        if(checkBirthDate(usrBean.getBirthDate()) == -1) {
            return false;
        }
        else {
            this.userModel.setCredentialsByBean(usrBean);
            this.userDao.registerUser(this.userModel);
            this.userModel.setId(userDao.getUserIDByUsername(this.userModel.getUserName()));
            usrBean.setUserID(userDao.getUserIDByUsername(this.userModel.getUserName()));
        }
        return true;
    }

    public ArrayList<String> getProvincesList(){
        ArrayList<String> provincesList;
        provincesList = this.locationDao.getProvincesList();
        return provincesList;
    }

    public ArrayList<String> getCitiesList(String selectedProvince){
        ArrayList<String> citiesList;
        citiesList = this.locationDao.getCitiesList(selectedProvince);
        return citiesList;
    }

    private int checkBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        // the user is for sure adult
        if(((today.getYear() - birthDate.getYear()) > 18) || ((today.getYear() - birthDate.getYear()) == 18 && birthDate.getDayOfMonth() <= today.getDayOfMonth() && birthDate.getMonthValue() <= today.getMonthValue())) {
            return 0;
        }
        return -1;
    }

    private void updateServerAfterUserReg(int userID){

        try {
            // Crea una socket per la connessione al server
            Socket socket = new Socket(Server.SERVER_ADDRESS, Server.PORT);

            // Ottiene il flusso di output della socket
            ObjectOutputStream objOutputStream = new ObjectOutputStream(socket.getOutputStream());

            //creazione messaggio
            Message message = new Message(MessageTypes.UserRegistration, userID, this.userModel.getCity());

            objOutputStream.writeObject(message);

            // Chiude la socket dopo l'invio della notifica
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
