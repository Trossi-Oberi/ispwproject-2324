package logic.graphiccontrollers;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;
public class GCSettingsOrg {

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
    void goToYourEventsOrg(MouseEvent event) {
        gui.changeGUI(event, "YourEventsOrg.fxml");
    }
}
