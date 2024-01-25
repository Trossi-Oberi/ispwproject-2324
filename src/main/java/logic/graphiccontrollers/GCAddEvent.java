package logic.graphiccontrollers;

import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import logic.view.EssentialGUI;

public class GCAddEvent {

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

    @FXML
    void goToYourEventsOrg(MouseEvent event) {
        gui.changeGUI(event, "YourEventsOrg.fxml");
    }

    @FXML
    void pickImage(MouseEvent event) {

    }

}
