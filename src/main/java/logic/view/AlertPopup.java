package logic.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.Alert;
import logic.utils.Alerts;


import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertPopup {
    private String imagePath;
    private String absolutePath;
    private Image image;
    private Logger logger;
    private Alert.AlertType type;

    private String headerText = null;

    public AlertPopup() {
        this.logger = Logger.getLogger("NightPlan");
    }

    public void displayAlertPopup(Alerts alertType, String text) {
        try {
            switch (alertType) {
                case ERROR:
                    type = Alert.AlertType.ERROR;
                    headerText = "A severe error has occurred";
                    break;
                case WARNING:
                    type = Alert.AlertType.WARNING;
                    headerText = "Warning";
                    break;
                case CONFIRMATION:
                    type = Alert.AlertType.CONFIRMATION;
                    break;
                case INFORMATION:
                    type = Alert.AlertType.INFORMATION;
                    headerText = "Alert";
                    break;
                default:
                    logger.log(Level.SEVERE, "Alert type not found");
                    break;
            }
        } catch (IllegalArgumentException e){
            logger.log(Level.SEVERE, "Image path not found!", e);
            return;
        }
        Alert alert = new Alert(type);
        alert.setTitle("NightPlan");
        alert.setContentText(text);
        alert.setHeaderText(headerText);
        //alert.showAndWait();
        alert.show();
    }
}