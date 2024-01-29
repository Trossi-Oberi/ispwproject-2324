package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

public class GCSettingsGeneral extends EssentialGUI { //SettingsGeneral coincide con SettingsOrg, SettingsUser ha le stesse funzionalita' di SettingsOrg + 2 pulsanti

    protected AlertPopup alert;

    @FXML
    private Button signOutButton;

    @FXML
    public void initialize() {
        alert = new AlertPopup();
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
    void goToHelpFaqs(MouseEvent event) {
        changeGUI(event, "HelpFAQs.fxml");
    }

    @FXML
    void leaveApp(MouseEvent event) {
        //LoggedUser.setStatus("Offline");
        changeGUI(event, "Login.fxml");
        this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged out successfully. You have been returned to the login screen!");
    }
}
