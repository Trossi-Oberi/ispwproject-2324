package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import logic.controllers.CFacade;
import logic.controllers.CLogin;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import logic.beans.BUserData;

public class GCLogin {
    private EssentialGUI gui;
    private AlertPopup alert;
    private CFacade facadeController;
    @FXML
    private Button GoogleLoginButton;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwd;
    @FXML
    private Text signUpText;
    @FXML
    private TextField usrname;

    private static final String IDLE_TEXT_STYLE = "-fx-background-color: transparent;";
    private static final String HOVERED_TEXT_STYLE = "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;";

    @FXML
    public void initialize() {

        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
    }

    public void registerControl(MouseEvent event){
        gui.changeGUI(event, "Registration.fxml");
    }

    public void loginControl(MouseEvent event){
        BUserData userBean = new BUserData(this.usrname.getText(), this.passwd.getText());

        if(facadeController.loginUser(userBean) == 1){
            switch(LoggedUser.getUserType()){
                case USER:
                    this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged in successfully as a user");
                    gui.changeGUI(event, "HomeUser.fxml");
//                    System.out.println("user logged with id "+LoggedUser.getUserID());
                    break;
                case ORGANIZER:
                    this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged in successfully as an organizer");
                    gui.changeGUI(event, "HomeOrg.fxml");
//                    System.out.println("user logged with id "+LoggedUser.getUserID());
                    break;
                default:
                    this.alert.displayAlertPopup(Alerts.ERROR,"FATAL: Cannot load HomePage from Login");
            }
        } else{
            this.alert.displayAlertPopup(Alerts.WARNING,"User not registered or wrong credentials. Please retry...");
        }

    }
}
