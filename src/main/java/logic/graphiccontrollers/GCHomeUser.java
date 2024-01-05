package logic.graphiccontrollers;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;

import logic.view.EssentialGUI;

public class GCHomeUser {
    private EssentialGUI gui;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
    }

    @FXML
    void showAbout(ActionEvent event) {

    }

}

