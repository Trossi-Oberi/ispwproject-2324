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

public class EssentialGUI extends Application {

    protected static final String APP_NAME = "NightPlan";
    protected static FXMLLoader loader = new FXMLLoader();
    protected static String sample;

    protected static Scene scene;
    protected static Logger logger = Logger.getLogger(APP_NAME);
    protected Image logoImage;
    protected String logoPath = "/icons/cuore.png";

    public EssentialGUI(){
        //da inserire cose...
    }

    @Override
    public void start(Stage stage){
        try{
            stage.setTitle(APP_NAME);
            String absolutePath = getClass().getResource(logoPath).toExternalForm();
            logoImage = new Image(absolutePath);
            stage.getIcons().add(logoImage);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e){
            logger.log(Level.SEVERE, "Cannot load GUI\n", e);
        }
    }

    public static void setScene(String newScene){
        sample = newScene;
    }

    public void nextGuiOnClick(MouseEvent event){
        Stage next = (Stage)((Node)event.getSource()).getScene().getWindow();
        start(next);
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

}
