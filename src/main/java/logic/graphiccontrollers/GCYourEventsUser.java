package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;

public class GCYourEventsUser {
    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void goToHomeUser(MouseEvent event) {
        gui.changeGUI(event, "HomeUser.fxml");
    }

    @FXML
    void goToNotifications(MouseEvent event) {
        gui.changeGUI(event, "Notifications.fxml");
    }

    @FXML
    void goToSettings(MouseEvent event) {
        gui.changeGUI(event, "SettingsUser.fxml");
    }
}
