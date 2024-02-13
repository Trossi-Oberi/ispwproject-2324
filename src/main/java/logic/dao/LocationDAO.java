package logic.dao;

import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class LocationDAO {
    public ArrayList<String> getProvincesList(){
        ArrayList<String> provincesList = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT name FROM provinces")) {
            try (ResultSet rs = statement.executeQuery()){
                while (rs.next()){
                    provincesList.add(rs.getString(1));
                }
            } catch (SQLException e){
                logger.log(Level.SEVERE, "SQLException occurred while executing query to DB to fetch provinces");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred during the fetch provinces from DB");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return provincesList;
    }

    public ArrayList<String> getCitiesList(String selectedProvince){
        ArrayList<String> citiesList = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT name FROM cities WHERE province = ?")) {
            statement.setString(1, selectedProvince);
            try (ResultSet rs = statement.executeQuery()){
                while (rs.next()){
                   citiesList.add(rs.getString(1));
                }
            } catch (SQLException e){
                logger.log(Level.SEVERE, "SQLException occurred while executing query to DB to fetch provinces");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred during the fetch provinces from DB");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return citiesList;
    }
}
