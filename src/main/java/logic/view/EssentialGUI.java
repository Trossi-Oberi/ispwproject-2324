package logic.view;

import java.io.IOException;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;


import javafx.stage.Stage;

//import logic.LoggerUser;


public class EssentialGUI extends Application {

    private static final String APP_NAME = "NightPlan";
    private static FXMLLoader loader = new FXMLLoader();
    private static String sample;
    private static Scene scene;
    private static Logger logger = Logger.getLogger(APP_NAME);



    public EssentialGUI(){
        //da inserire cose...
    }

    @Override
    public void start(Stage stage){
        try{
            stage.setTitle(APP_NAME);
            stage.setScene(scene);
            stage.show();

        } catch(Exception e){
            logger.log(Level.SEVERE, "Cannot load GUI\n", e);
        }
    }

    public static void setScene(String newScene){
        sample = newScene;
    }

    public static void loadApp(){
        try {
            loader = new FXMLLoader();
            URL loc = EssentialGUI.class.getResource(sample);
            Parent root = loader.load(loc);
            scene = new Scene(root);
            scene.getStylesheets().add(EssentialGUI.class.getResource("application.css").toExternalForm());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        }
    }

    public static void main(String[] args) {
        setScene("Login.fxml");
        loadApp();
        launch(args);
    }


}
