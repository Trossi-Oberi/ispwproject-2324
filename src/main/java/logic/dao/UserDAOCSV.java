package logic.dao;

import logic.controllers.ObserverClass;
import logic.model.MUser;

import java.util.List;
import java.util.Map;

public class UserDAOCSV implements UserDAO{
    @Override
    public int checkLoginInfo(MUser usrMod, boolean isGoogleAccount) {
        return 0;
    }

    @Override
    public String getUserCityByID(int usrId) {
        return null;
    }

    @Override
    public void registerUser(MUser usrModel) {

    }

    @Override
    public int getUserIDByUsername(String username) {
        return 0;
    }

    @Override
    public void setStatus(int userID) {

    }

    @Override
    public int changeCity(int userID, String province, String city) {
        return 0;
    }

    @Override
    public String getUsernameByID(int userID) {
        return null;
    }

    //gestiti dal server

    @Override
    public void populateObsByCity(Map<String, List<ObserverClass>> obsByCity) {

    }

    @Override
    public void populateConnUsers(Map<Integer, Boolean> connUsers) {

    }

    @Override
    public void populateConnOrganizers(Map<Integer, Boolean> connOrganizers) {

    }
}
