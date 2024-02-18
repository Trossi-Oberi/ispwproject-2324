package logic.dao;

import logic.controllers.ObserverClass;
import logic.model.MUser;

import java.util.List;
import java.util.Map;

public interface UserDAO {
    int checkLoginInfo(MUser usrMod, boolean isGoogleAccount);
    String getUserCityByID(int usrId);
    void registerUser(MUser usrModel);
    int getUserIDByUsername(String username);
    void setStatus(int userID);
    int changeCity(int userID, String province, String city);
    String getUsernameByID(int userID);
    void populateObsByCity(Map<String, List<ObserverClass>> obsByCity);
    void populateConnUsers(Map<Integer, Boolean> connUsers);
    void populateConnOrganizers(Map<Integer, Boolean> connOrganizers);
    
}
