package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import logic.utils.LoggedUser;
import logic.utils.UserTypes;

public class GCProfile {

    @FXML
    private Text userName;

    @FXML
    private Text firstName;

    @FXML
    private Text lastName;

    @FXML
    private Text gender;

    @FXML
    private Text birthDate;

    @FXML
    private Text userType;

    @FXML
    private Button goBackBtn;

    private AlertPopup alert;
    private EssentialGUI gui;
    private UserTypes type;

    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        switch(LoggedUser.getUserType()){
            case USER:
                this.type = UserTypes.USER;
                break;
            case ORGANIZER:
                this.type = UserTypes.ORGANIZER;
                break;
        }
        this.userName.setText(LoggedUser.getUserName());
        this.firstName.setText(LoggedUser.getFirstName());
        this.lastName.setText(LoggedUser.getLastName());
        this.birthDate.setText(LoggedUser.getBirthDate());
        this.gender.setText(LoggedUser.getGender());
        this.userType.setText(type.toString());
    }

    @FXML
    void goBack(MouseEvent event) {
        switch (type){
            case USER:
                gui.changeGUI(event, "SettingsUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "SettingsOrg.fxml");
                break;
        }
    }

    @FXML
    void goToHomeUser(MouseEvent event) {
        switch (type){
            case USER:
                gui.changeGUI(event, "HomeUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "HomeOrg.fxml");
                break;
        }
    }

    @FXML
    void goToNotifications(MouseEvent event) {
        gui.changeGUI(event, "Notifications.fxml");
    }

    @FXML
    void goToYourEventsUser(MouseEvent event) {
        switch (type){
            case USER:
                gui.changeGUI(event, "YourEventsUser.fxml");
                break;
            case ORGANIZER:
                gui.changeGUI(event, "YourEventsOrg.fxml");
                break;
        }
    }

}
