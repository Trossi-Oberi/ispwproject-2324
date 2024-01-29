package logic.view;

import java.io.IOException;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import logic.utils.LoggedUser;
import logic.controllers.CFacade;

public class EssentialGUI extends Application {

    private static final String APP_NAME = "NightPlan";
//    private static FXMLLoader loader = new FXMLLoader();
    protected static String sceneName;

    protected static Scene scene;
    protected static final Logger logger = Logger.getLogger(APP_NAME);
    protected CFacade cfacade;

    public EssentialGUI(){
        this.cfacade = new CFacade();
    }

    @Override
    public void start(Stage stage){
        try{
            stage.setTitle(APP_NAME);
            String logoPath = "/icons/cuore.png";
            String absolutePath = getClass().getResource(logoPath).toExternalForm();
            //gestire NullPointerException
            Image logoImage = new Image(absolutePath);
            stage.getIcons().add(logoImage);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e){
            logger.log(Level.SEVERE, "Cannot load GUI\n", e);
        }
    }

    public static void setScene(String newScene){
        sceneName = newScene;
    }

    public void nextGuiOnClick(MouseEvent event){
        Stage next = (Stage)((Node)event.getSource()).getScene().getWindow();
        start(next);
    }

    public static void loadApp(){
        try {
//            loader = new FXMLLoader();
            URL loc = EssentialGUI.class.getResource(sceneName);
            Parent root = FXMLLoader.load(loc); //checkare se loc!=null
            scene = new Scene(root);
            scene.getStylesheets().add(EssentialGUI.class.getResource("application.css").toExternalForm());
            //gestire NullPointerException
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        }
    }


    public void changeGUI(MouseEvent event, String newScene){
        setScene(newScene);
        loadApp();
        nextGuiOnClick(event);
    }

    public static void main(String[] args) {
        setScene("Login.fxml");
        loadApp();
        launch(args);
    }

    public void goToHome(MouseEvent event){
        switch(LoggedUser.getUserType()){
            case USER:
                changeGUI(event, "HomeUser.fxml");
                break;
            case ORGANIZER:
                changeGUI(event, "HomeOrg.fxml");
                break;
        }

    }

    public void goToNotifications(MouseEvent event){
        changeGUI(event, "Notifications.fxml");
    }

    public void goToYourEvents(MouseEvent event){
        switch(LoggedUser.getUserType()){
            case USER:
                changeGUI(event, "YourEventsUser.fxml");
                break;
            case ORGANIZER:
                changeGUI(event, "YourEventsOrg.fxml");
                break;
        }
    }

    public void goToSettings(MouseEvent event){
        switch(LoggedUser.getUserType()){
            case USER:
                changeGUI(event, "SettingsUser.fxml");
                break;
            case ORGANIZER:
                changeGUI(event, "SettingsOrg.fxml");
                break;
        }
    }



}
