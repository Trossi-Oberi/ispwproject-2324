package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.view.EssentialGUI;

import javafx.scene.input.MouseEvent;

public class GCHomeOrg {
    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }


    @FXML
    void goToNotifications(MouseEvent event) {
        gui.changeGUI(event, "Notifications.fxml");
    }

    @FXML
    void goToSettingsOrg(MouseEvent event) {
        gui.changeGUI(event, "SettingsOrg.fxml");
    }

    @FXML
    void goToYourEventsOrg(MouseEvent event) {
        gui.changeGUI(event, "YourEventsOrg.fxml");
    }
}
