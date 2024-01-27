package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;
public class GCSettingsOrg {

    private EssentialGUI gui;
    private AlertPopup alert;

    @FXML
    private Button yourProfileBtn;
    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
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

    @FXML
    void goToProfile(MouseEvent event) {
        gui.changeGUI(event, "Profile.fxml");
    }

    @FXML
    void leaveApp(MouseEvent event) {
        //LoggedUser.setStatus("Offline");
        gui.changeGUI(event, "Login.fxml");
        this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged out successfully. You have been returned to the login screen!");
    }

}
