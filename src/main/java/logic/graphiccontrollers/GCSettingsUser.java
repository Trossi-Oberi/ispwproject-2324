package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import logic.utils.Alerts;

public class GCSettingsUser extends GCSettingsGeneral {
    @FXML
    private Button changeCityBtn;

    @FXML
    private Button yourGroupsBtn;

    @FXML
    void goToChangeCity(MouseEvent event) {
        changeGUI(event, "EditCity.fxml");
    }

    @FXML
    void goToYourGroups(MouseEvent event) {
        alert.displayAlertPopup(Alerts.ERROR, "Not implemented yet :S");
    }
}
