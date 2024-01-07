package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;


public class GCYourEventsOrg {

    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void goToHomeOrg(MouseEvent event) {
        gui.changeGUI(event, "HomeOrg.fxml");
    }

    @FXML
    void goToNotifications(MouseEvent event) {
        gui.changeGUI(event, "Notifications.fxml");
    }

    @FXML
    void goToSettingsOrg(MouseEvent event) {
        gui.changeGUI(event, "SettingsOrg.fxml");
    }

}
