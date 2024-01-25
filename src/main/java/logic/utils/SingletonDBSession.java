package logic.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonDBSession {
    private static SingletonDBSession istance = null;
    private Logger logger;
    protected Connection connection = null;

    //jdbc:mysql://localhost/whereigo?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String url = "jdbc:mysql://localhost/nightplan";
    private String username = "root";
    private String password = "Trossioberi2024!";


    protected SingletonDBSession() {
        this.logger = Logger.getLogger("NightPlan");
    }

    public Connection getConnection() {
        try{
            this.connection = DriverManager.getConnection(url, username, password);
            return this.connection;
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "Connection to DB failed !", e);
            return null;
        }
    }

    public static synchronized SingletonDBSession getInstance(){
        //singleton method
        if(SingletonDBSession.istance == null) {
            SingletonDBSession.istance = new SingletonDBSession();
        }
        return SingletonDBSession.istance;
    }

    public void closeConn() {
        try {
            this.connection.close();
        } catch(SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
