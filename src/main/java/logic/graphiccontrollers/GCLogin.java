package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import logic.controllers.CFacade;
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

    private String authCode = "";

    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
    }

    @FXML
    public void registerControl(MouseEvent event){
        gui.changeGUI(event, "ClassicRegistration.fxml");
    }

    @FXML
    public void loginControl(MouseEvent event){
        BUserData userBean = new BUserData(this.usrname.getText(), this.passwd.getText());

        if(facadeController.loginUser(userBean) == 1){
            switch(LoggedUser.getUserType()){
                case USER:
                    this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged in successfully as a user");
                    gui.changeGUI(event, "HomeUser.fxml");
                    break;
                case ORGANIZER:
                    this.alert.displayAlertPopup(Alerts.INFORMATION,"Logged in successfully as an organizer");
                    gui.changeGUI(event, "HomeOrg.fxml");
                    break;
                default:
                    this.alert.displayAlertPopup(Alerts.ERROR,"FATAL: Cannot load HomePage from Login");
            }
        } else{
            this.alert.displayAlertPopup(Alerts.WARNING,"User not registered or wrong credentials. Please retry...");
        }
    }

    @FXML
    public void googleLoginControl(MouseEvent event) {
        BUserData userBean = new BUserData();
        try {
            if (facadeController.initGoogleAuth() == 1) {
                openAuthCodeWindow();
                if (facadeController.googleLoginUser(userBean, this.authCode) == 1) {
                    switch (LoggedUser.getUserType()) {
                        case USER:
                            this.alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as a user");
                            gui.changeGUI(event, "HomeUser.fxml");
                            break;
                        case ORGANIZER:
                            this.alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as an organizer");
                            gui.changeGUI(event, "HomeOrg.fxml");
                            break;
                        default:
                            this.alert.displayAlertPopup(Alerts.ERROR, "FATAL: Cannot load HomePage from Login");
                    }
                } else {
                    this.alert.displayAlertPopup(Alerts.WARNING, "User not registered using Google Auth! \nYou will be redirected to Registration Page");
                    LoggedUser.setUserName(userBean.getUsername());
                    gui.changeGUI(event, "GoogleRegistration.fxml");
                }
            }
        } catch (RuntimeException e){
            this.alert.displayAlertPopup(Alerts.ERROR, "Runtime exception in initGoogleAuth function");
        }
    }

    private void openAuthCodeWindow(){
        // Creazione di una nuova finestra per la modalità
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Insert auth code from Google");

        // Creazione di un campo di testo e un pulsante nella finestra modale
        TextField textField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            this.authCode = textField.getText();
            modalStage.close();
        });
        // Layout della finestra modale
        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(10));
        modalLayout.getChildren().addAll(textField, sendButton);

        // Creazione della scena per la finestra modale
        Scene modalScene = new Scene(modalLayout, 250, 150);

        // Disabilita il pulsante di chiusura predefinito
        modalStage.setOnCloseRequest(WindowEvent::consume);

        modalStage.setScene(modalScene);

        // Mostra la finestra modale
        modalStage.showAndWait();
    }
}
