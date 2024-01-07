package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;

public class GCSettingsUser {
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
    void goToYourEventsUser(MouseEvent event) {
        gui.changeGUI(event, "YourEventsUser.fxml");
    }
}
