package logic.view;

import javafx.scene.control.Alert;

import java.util.logging.Level;

import logic.utils.Alerts;

import static logic.view.EssentialGUI.logger;


public class AlertPopup {
    private Alert.AlertType type;

    private String headerText = null;

    public AlertPopup() {
        //empty
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

            Alert alert = new Alert(type);
            alert.setTitle("NightPlan");
            alert.setContentText(text);
            alert.setHeaderText(headerText);
            alert.show();
        } catch (IllegalStateException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}