package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;

public class GCNotifications {
    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void goToEvents(MouseEvent event) {
        switch (LoggedUser.getUserType()){
            case USER:
                gui.changeGUI(event, "YourEventsUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "YourEventsOrg.fxml");
                break;
        }
    }

    @FXML
    void goToHome(MouseEvent event) {
        switch (LoggedUser.getUserType()){
            case USER:
                gui.changeGUI(event, "HomeUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "HomeOrg.fxml");
                break;
        }
    }

    @FXML
    void goToSettings(MouseEvent event) {
        switch (LoggedUser.getUserType()){
            case USER:
                gui.changeGUI(event, "SettingsUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "SettingsOrg.fxml");
                break;
        }
    }
}
