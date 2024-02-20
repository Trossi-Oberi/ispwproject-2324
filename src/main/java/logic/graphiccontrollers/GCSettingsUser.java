package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

public class GCSettingsUser extends GCSettingsGeneral {
    @FXML
    private Button changeCityBtn;

    @FXML
    void goToChangeCity(MouseEvent event) {
        changeGUI(event, "EditCity.fxml");
    }

}
