package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import javafx.event.ActionEvent;

import javafx.scene.input.MouseEvent;

public class GCHomeOrg extends EssentialGUI {
    @FXML
    void goToAddEvent(MouseEvent event) {
        changeGUI(event, "AddEvent.fxml");
    }
}
