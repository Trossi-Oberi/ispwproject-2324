package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import logic.controllers.CLogin;
import logic.view.ErrorPopup;
import logic.view.EssentialGUI;

import logic.beans.BUserData;

import java.io.IOException;

public class GCLogin {
    private EssentialGUI gui;
    private ErrorPopup errorLogin;

    private CLogin loginController;

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
        this.errorLogin = new ErrorPopup();
        this.gui = new EssentialGUI();
        this.loginController = new CLogin();
    }

    public void registerControl(MouseEvent event) throws IOException {
        gui.changeGUI(event, "Registration.fxml");

    }

    public void loginControl(MouseEvent event){
        BUserData userBean = new BUserData(this.usrname.getText(), this.passwd.getText());
        if(this.loginController.checkLogInControl(userBean) == 1){
            gui.changeGUI(event, "HomeUser.fxml"); //da implementare se user HomeUser fxml altrimenti HomeOrg per organizer
        } else{
            //display error
            this.errorLogin.displayErrorPopup("User not registered or wrong credentials. Please retry...");
        }

    }
}
