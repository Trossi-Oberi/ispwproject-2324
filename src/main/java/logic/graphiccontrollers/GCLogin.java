package logic.graphiccontrollers;

import javafx.fxml.FXML;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import logic.view.EssentialGUI;

import java.io.IOException;

public class GCLogin {
    private EssentialGUI gui;

    @FXML
    private Text signUpText;
    private static final String IDLE_TEXT_STYLE = "-fx-background-color: transparent;";
    private static final String HOVERED_TEXT_STYLE = "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;";

    @FXML
    public void initialize() {
        gui = new EssentialGUI();
    }

    public void registerControl(MouseEvent event) throws IOException {
        gui.changeGUI(event, "Registration.fxml");

    }

    public void loginControl(MouseEvent event){
        if(true){
            gui.changeGUI(event, "Home.fxml");
        } else{
            //display error

        }

    }
}
