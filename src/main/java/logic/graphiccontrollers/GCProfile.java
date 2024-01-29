package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import logic.utils.LoggedUser;
import logic.utils.UserTypes;

public class GCProfile extends EssentialGUI{

    @FXML
    private Text userName;

    @FXML
    private Text firstName;

    @FXML
    private Text lastName;

    @FXML
    private Text gender;
    @FXML
    private Text city;

    @FXML
    private Text birthDate;

    @FXML
    private Text userType;

    @FXML
    private Button goBackBtn;

    private AlertPopup alert;
    private UserTypes type;

    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
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
        this.city.setText(LoggedUser.getCity());
        this.userType.setText(type.toString());
    }

}
