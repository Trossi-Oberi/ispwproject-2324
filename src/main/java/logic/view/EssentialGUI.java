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

    private static final String APP_NAME = "NightPlan";
//    private static FXMLLoader loader = new FXMLLoader();
    private static String sceneName;

    private static Scene scene;
    private static final Logger logger = Logger.getLogger(APP_NAME);

    public EssentialGUI(){
        //da inserire cose...
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

}
