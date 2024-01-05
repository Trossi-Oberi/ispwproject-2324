package logic.graphiccontrollers;

import javafx.fxml.FXML;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

import logic.view.EssentialGUI;

import java.io.IOException;

public class GCHome {
    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void showAbout(ActionEvent event) {

    }

}

