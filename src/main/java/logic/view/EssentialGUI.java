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
import logic.utils.*;
import logic.controllers.CFacade;

public class EssentialGUI extends Application implements NotificationView {
    private static final String APP_NAME = "NightPlan";
    private static final String PATH = "icons";
    private static final String LOGO_NAME = "favicon.png";
    protected static String sceneName;
    protected static Scene scene;
    protected static CFacade cfacade = new CFacade();
    protected AlertPopup alert;
    protected static EssentialGUI gui = new EssentialGUI();

    //dichiaro logger pubblico, globale e costante
    public static final Logger logger = Logger.getLogger(APP_NAME);

    public EssentialGUI() {
        this.alert = new AlertPopup();
    }

    @Override
    public void start(Stage stage) {
        try {
            CFacade.setNotiGraphic(this);
            stage.setTitle(APP_NAME);
            String absolutePath = setAbsolutePath();
            Image logoImage = new Image(absolutePath);
            stage.getIcons().add(logoImage);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load absolute path of app icon\n", e);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Cannot load EssentialGUI due to illegal argument into logo image\n", e);
        }
    }

    private String setAbsolutePath() {
        try {
            return getClass().getResource("/" + PATH + "/" + LOGO_NAME).toExternalForm();
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    public static void setScene(String newScene) {
        sceneName = newScene;
    }

    public void nextGuiOnClick(MouseEvent event) {
        Stage next = (Stage) ((Node) event.getSource()).getScene().getWindow();
        start(next);
    }

    public static void loadApp() {
        try {
            URL loc = EssentialGUI.class.getResource(sceneName);
            Parent root = null;
            if (loc != null) {
                root = FXMLLoader.load(loc);
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        }
    }


    public void changeGUI(MouseEvent event, String newScene) {
        setScene(newScene);
        loadApp();
        nextGuiOnClick(event);
    }

    public static void main(String[] args) {
        setScene("Login.fxml");
        loadApp();

        if (args.length > 0) {
            if ("JDBC".equals(args[0])) {
                logger.info("NightPlan started with JDBC persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
            } else if ("FileSystem".equals(args[0])) {
                logger.info("NightPlan started with FileSystem persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.FILE_SYSTEM);
            }
        } else {
            logger.info("NightPlan started with default persistence logic (JDBC)");
            PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
        }

        launch(args);
    }

    public void goToHome(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "HomeUser.fxml");
        } else {
            changeGUI(event, "HomeOrg.fxml");
        }
    }

    public void goToNotifications(MouseEvent event) {
        changeGUI(event, "Notifications.fxml");
    }

    public void goToYourEvents(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "YourEventsUser.fxml");
        } else {
            changeGUI(event, "YourEventsOrg.fxml");
        }
    }

    public void goToSettings(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "SettingsUser.fxml");
        } else {
            changeGUI(event, "SettingsOrg.fxml");
        }
    }

    @Override
    public void showNotification(NotificationTypes type) {
        Platform.runLater(() -> {
            if (type.equals(NotificationTypes.EVENT_ADDED)) {
                alert.displayAlertPopup(Alerts.INFORMATION, "New event in your city!\nCheck your events page.");
            } else if (type.equals(NotificationTypes.USER_EVENT_PARTICIPATION)) {
                alert.displayAlertPopup(Alerts.INFORMATION, "New user participating to your event.");
            }
        });
    }
}
