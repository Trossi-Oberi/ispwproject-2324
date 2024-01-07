package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import logic.view.EssentialGUI;

import java.io.IOException;

public class GCRegistration {

    private EssentialGUI gui;

    @FXML
    private TextField confirmEmailField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerBtn;

    @FXML
    private Button returnLogin;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void returnBack(MouseEvent event) throws IOException {
        gui.changeGUI(event, "Login.fxml");
    }

}
