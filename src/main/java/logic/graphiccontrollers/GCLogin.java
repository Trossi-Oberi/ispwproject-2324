package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import logic.exceptions.InvalidTokenValue;
import logic.utils.Alerts;
import logic.utils.GoogleLogin;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import logic.beans.BUserData;

public class GCLogin extends EssentialGUI{
    @FXML
    private Button googleLoginButton;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwd;
    @FXML
    private Text signUpText;
    @FXML
    private TextField usrname;

    private String authCode;
    private boolean isGoogleAuth;

    @FXML
    public void initialize() {
        this.authCode = null;
        this.isGoogleAuth = false;
    }

    @FXML
    public void registerControl(MouseEvent event){
        changeGUI(event, "ClassicRegistration.fxml");
    }

    @FXML
    public void loginControl(MouseEvent event){
        BUserData userBean;
        try {
            if (isGoogleAuth) {
                //google login
                userBean = new BUserData();
                setupGoogleLogin();
            } else {
                //classic login
                userBean = new BUserData(this.usrname.getText(), this.passwd.getText());
            }
            if (cfacade.loginUser(userBean, this.isGoogleAuth, authCode) == 1) {
                switch (LoggedUser.getUserType()) {
                    case USER:
                        alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as a user");
                        changeGUI(event, "HomeUser.fxml");
                        break;
                    case ORGANIZER:
                        alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as an organizer");
                        changeGUI(event, "HomeOrg.fxml");
                        break;
                    default:
                        alert.displayAlertPopup(Alerts.ERROR, "Cannot load home page from login");
                }
            } else {
                if (!isGoogleAuth) {
                    alert.displayAlertPopup(Alerts.WARNING, "User not registered or wrong credentials. Please retry...");
                } else {
                    alert.displayAlertPopup(Alerts.WARNING, "User not registered using Google Auth! \nYou will be redirected to Registration Page");
                    LoggedUser.setUserName(userBean.getUsername());
                    changeGUI(event, "GoogleRegistration.fxml");
                }
            }
        } catch (InvalidTokenValue e) {
            alert.displayAlertPopup(Alerts.WARNING, "Invalid authorization code. Please retry...");
        } catch (RuntimeException e){
            alert.displayAlertPopup(Alerts.ERROR, "Runtime exception: " + e.getMessage());
        }
    }

    private void setupGoogleLogin() {
        try {
            if(GoogleLogin.initGoogleLogin() == 1){
                openAuthCodeWindow();
            }
        } catch (Exception e) {
            logger.severe(() -> "Error during google login setup "+e.getMessage());
        }
    }


    @FXML
    public void googleLoginControl(MouseEvent event){
        this.isGoogleAuth = true;
        loginControl(event);
    }

    private void openAuthCodeWindow(){
        // Creazione di una nuova finestra per la modalità
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Google Authentication Token");
        modalStage.setResizable(false);

        // Creazione di un campo di testo e un pulsante nella finestra modale
        Label text = new Label("Please write down the authentication token:");
        TextField textField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            this.authCode = textField.getText();
            modalStage.close();
        });
        // Layout della finestra modale
        AnchorPane modalLayout = new AnchorPane();
        modalLayout.setPadding(new Insets(10));
        modalLayout.getChildren().addAll(text, textField, sendButton);

        // Ancoraggio del campo di testo
        AnchorPane.setTopAnchor(text, 10.0);
        AnchorPane.setLeftAnchor(text, 10.0);

        // Ancoraggio del campo di testo
        AnchorPane.setTopAnchor(textField, 40.0);
        AnchorPane.setLeftAnchor(textField, 10.0);
        AnchorPane.setRightAnchor(textField, 10.0);

        // Ancoraggio del pulsante
        AnchorPane.setBottomAnchor(sendButton, 10.0);
        AnchorPane.setRightAnchor(sendButton, 10.0);

        // Creazione della scena per la finestra modale
        Scene modalScene = new Scene(modalLayout, 400, 130);

        // Disabilita il pulsante di chiusura predefinito
        modalStage.setOnCloseRequest(WindowEvent::consume);
        modalStage.setScene(modalScene);

        // Mostra la finestra modale
        modalStage.showAndWait();
    }
}
