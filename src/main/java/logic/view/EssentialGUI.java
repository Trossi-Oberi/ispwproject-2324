package logic.view;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/*import logic.LoggedUser;
import logic.UserType;
import logic.controllers.ControllerFacade;*/

public class EssentialGUI extends Application {

    @FXML
    protected ImageView home;
    @FXML
    protected ImageView keys;
    @FXML
    protected ImageView chat;
    @FXML
    protected ImageView bookTravel;
    @FXML
    protected ImageView settings;
    @FXML
    protected ImageView userImage;
    @FXML
    protected ImageView exit;

    protected static FXMLLoader loader = new FXMLLoader();
    protected static String sample;
    protected static Scene scene;
    protected static Logger logger = Logger.getLogger("WIG");
//    protected ControllerFacade facade;
//    protected ErrorPopup p

    public EssentialGUI(){

    }

    public static void main(String[] args) {
        setScene("Login.fxml");
        loadScene();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot load GUI\n", e);
        }
    }

    public static void setScene(String newScene) {
        sample = newScene;
    }

    public static void loadScene() {
        try {
            loader = new FXMLLoader();
            URL loc = EssentialGUI.class.getResource(sample);
            loader.setLocation(loc);
            Parent newSceneParent = loader.load();
            scene = new Scene(newSceneParent);
            scene.getStylesheets().add(EssentialGUI.class.getResource("").toExternalForm());
        } catch (IOException e) {
//            logger.log(Level.SEVERE, "Cannot load the scene\n", e);
        }
    }
}
