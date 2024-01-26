package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import logic.utils.MusicGenres;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.stage.FileChooser;

public class GCAddEvent {

    ObservableList<String> musicGenresList = FXCollections.observableArrayList(MusicGenres.musicgenresarr);

    private EssentialGUI gui;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField eventLocTF;

    @FXML
    private TextField eventNameTF;

    @FXML
    private Button fileChooserBtn;

    @FXML
    private ChoiceBox<String> musicGenreBox;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();
        musicGenreBox.setValue(MusicGenres.musicgenresarr[0]);
        musicGenreBox.setItems(musicGenresList);
    }

    @FXML
    void goToHomeOrg(MouseEvent event) {
        gui.changeGUI(event, "HomeOrg.fxml");
    }

    @FXML
    void goToNotifications(MouseEvent event) {
        gui.changeGUI(event, "Notifications.fxml");
    }

    @FXML
    void goToSettingsOrg(MouseEvent event) {
        gui.changeGUI(event, "SettingsOrg.fxml");
    }

    @FXML
    void goToYourEventsOrg(MouseEvent event) {
        gui.changeGUI(event, "YourEventsOrg.fxml");
    }

    @FXML
    void pickImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
    }

    @FXML
    void getDate(ActionEvent event) {
        LocalDate myDate = datePicker.getValue();
        String myFormattedDate = myDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

}
