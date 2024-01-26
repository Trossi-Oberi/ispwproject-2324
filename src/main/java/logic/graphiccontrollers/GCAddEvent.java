package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import logic.utils.MusicGenres;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;




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
    private TextField eventTimeHour;

    @FXML
    private TextField eventTimeMinutes;

    @FXML
    private Button fileChooserBtn;

    @FXML
    private Label pickedFileLabel;

    @FXML
    private ChoiceBox<String> musicGenreBox;

    @FXML
    public void initialize() {
        this.gui = new EssentialGUI();

        //Disabilita le date precedenti a quella odierna per il datePicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
        
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
        ExtensionFilter imageFilter = new ExtensionFilter("Immagini (*.png, *.jpg)", "*.png", "*jpg");
        fc.getExtensionFilters().addAll(imageFilter);
        File selectedFile = fc.showOpenDialog(null);

        //Controllare se il file e' diverso da null
        //if (selectedFile!=null){
        pickedFileLabel.setText(selectedFile.getName());
        //}*/

    }

    @FXML
    void getDate(ActionEvent event) {

        LocalDate myDate = datePicker.getValue();
        String myFormattedDate = myDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

}
