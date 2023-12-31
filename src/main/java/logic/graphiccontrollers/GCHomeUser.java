package logic.graphiccontrollers;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;

public class GCHomeUser {
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
    void goToSettingsUser(MouseEvent event) {
        gui.changeGUI(event, "SettingsUser.fxml");
    }

    @FXML
    void goToYourEventsUser(MouseEvent event) {
        gui.changeGUI(event, "YourEventsUser.fxml");
    }

}

