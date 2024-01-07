package logic.graphiccontrollers;

import javafx.fxml.FXML;
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
        //adottare soluzione polimorfica, se utente loggato = user vado sulle grafiche user
        //altrimenti se organizer vado sulle grafiche organizer
        gui.changeGUI(event, "YourEventsOrg.fxml"); //giusto per provare
    }

    @FXML
    void goToHome(MouseEvent event) {
        //adottare soluzione polimorfica, se utente loggato = user vado sulle grafiche user
        //altrimenti se organizer vado sulle grafiche organizer
        gui.changeGUI(event, "HomeOrg.fxml"); //giusto per provare
    }

    @FXML
    void goToSettings(MouseEvent event) {
        //adottare soluzione polimorfica, se utente loggato = user vado sulle grafiche user
        //altrimenti se organizer vado sulle grafiche organizer
        gui.changeGUI(event, "SettingsOrg.fxml"); //giusto per provare
    }
}
