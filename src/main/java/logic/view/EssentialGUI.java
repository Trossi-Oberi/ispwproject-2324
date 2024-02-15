package logic.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.controllers.CFacade;
import logic.utils.NotificationTypes;

public class EssentialGUI extends Application {
    //TODO: inserire le icone in tutta l'applicazione
    private static final String APP_NAME = "NightPlan";
    protected static String sceneName;
    protected static Scene scene;
    protected static CFacade cfacade = new CFacade();
    protected AlertPopup alert;

    //dichiaro logger pubblico, globale e costante
    public static final Logger logger = Logger.getLogger(APP_NAME);

    public EssentialGUI(){
        this.alert = new AlertPopup();
    }

    @Override
    public void start(Stage stage){
        try{
            stage.setTitle(APP_NAME);
            String logoPath = "/icons/cuore.png";
            String absolutePath;
            try {
                absolutePath = getClass().getResource(logoPath).toExternalForm();
            } catch (NullPointerException e){
                throw new NullPointerException();
            }
            Image logoImage = new Image(absolutePath);
            stage.getIcons().add(logoImage);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch(NullPointerException e){
            logger.log(Level.SEVERE, "Cannot load absolute path of app icon\n", e);
        } catch(IllegalArgumentException e){
            logger.log(Level.SEVERE, "Cannot load EssentialGUI due to illegal argument into logo image\n", e);
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
            URL loc = EssentialGUI.class.getResource(sceneName);
            Parent root = null;
            if(loc != null) {
                root = FXMLLoader.load(loc);
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException | NullPointerException e) {
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


    public static void showNotification(NotificationTypes type) {
        Platform.runLater(() -> {
            AlertPopup alert = new AlertPopup();
            switch (type) {
                case EventAdded:
                    alert.displayAlertPopup(Alerts.INFORMATION, "New event in your city!\nCheck your events page.");
//                    if (loc.toExternalForm().contains("HomeUser.fxml")) {
//                    //TODO: forse un giorno faremo l'aggiornamento dinamico
//                    }
                    break;
                case UserEventParticipation:
                    alert.displayAlertPopup(Alerts.INFORMATION, "New user participating to your event."); //TODO: nel caso aggiungere anche il nome evento o l'id evento (NON IMPORTANTE)
                    break;
            }
        });
    }
}
