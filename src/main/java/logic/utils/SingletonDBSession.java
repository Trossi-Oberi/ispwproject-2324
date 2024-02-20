package logic.utils;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;


public class SingletonDBSession {
    private static SingletonDBSession instance;
    private String username;
    private String password;
    private final String url;
    private InputStream inputStream;
    protected Connection connection = null;


    public String[] getPropValues() throws IOException {
        String[] result = new String[2];
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            prop.load(inputStream);
            result[0] = prop.getProperty("username");
            result[1] = prop.getProperty("password");
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Properties file not found!", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Properties cannot be loaded!", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }

    private SingletonDBSession() {
        this.url = "jdbc:mysql://localhost/nightplan";
        try {
            this.username = getPropValues()[0];
            this.password = getPropValues()[1];
        } catch (IOException e) {
            logger.severe("Failed to get username and password from config.properties file");
        }
    }

    public Connection getConnection() {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
            return this.connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection to DB failed!", e);
            return null;
        }
    }

    public static synchronized SingletonDBSession getInstance() {
        //singleton method
        if (instance == null) {
            instance = new SingletonDBSession();
        }
        return instance;
    }

    public void closeConn() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
