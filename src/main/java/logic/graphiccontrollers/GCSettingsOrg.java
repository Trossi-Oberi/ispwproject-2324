package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;
public class GCSettingsOrg extends EssentialGUI{
    private AlertPopup alert;

    @FXML
    private Button yourProfileBtn;
    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
    }

    @FXML
    void goToProfile(MouseEvent event) {
        changeGUI(event, "Profile.fxml");
    }

    @FXML
    void goToContactUs(MouseEvent event) {
        changeGUI(event, "ContactUs.fxml");
    }

    @FXML
    void leaveApp(MouseEvent event) {
        //LoggedUser.setStatus("Offline");
        changeGUI(event, "Login.fxml");
        this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged out successfully. You have been returned to the login screen!");
    }

}
