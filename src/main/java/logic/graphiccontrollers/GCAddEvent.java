package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import logic.utils.MusicGenres;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import logic.beans.BEvent;
import logic.controllers.CFacade;


public class GCAddEvent extends EssentialGUI {

    ObservableList<String> musicGenresList = FXCollections.observableArrayList(MusicGenres.MUSIC_GENRES);
    private CFacade facade = new CFacade();
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField eventAddressTF;
    @FXML
    private TextField eventCityTF;
    @FXML
    private TextField eventNameTF;
    @FXML
    private TextField eventHourTF;

    @FXML
    private TextField eventMinutesTF;
    @FXML
    private Button fileChooserBtn;
    @FXML
    private Label pickedFileLabel;
    @FXML
    private ChoiceBox<String> musicGenreBox;

    private BEvent eventBean;
    private byte[] eventPicData;

    public GCAddEvent() {
        eventBean = new BEvent();
    }

    @FXML
    public void initialize() {
        //Disabilita le date precedenti a quella odierna per il datePicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });

        musicGenreBox.setValue(MusicGenres.MUSIC_GENRES[0]);
        musicGenreBox.setItems(musicGenresList);
    }

    @FXML
    void pickImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        ExtensionFilter imageFilter = new ExtensionFilter("Immagini (*.png, *.jpg)", "*.png", "*jpg");
        fc.getExtensionFilters().addAll(imageFilter);
        File eventPicFile = fc.showOpenDialog(null);

        if (eventPicFile != null) {
            pickedFileLabel.setText(eventPicFile.getName());
        }

        //conversione da file ad array di bytes
        if (eventPicFile !=null){
            try {
                this.eventPicData = Files.readAllBytes(eventPicFile.toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

    }

    @FXML
        //azioni da eseguire quando clicko pulsante "CONFIRM"
    void addEventControl(MouseEvent event) {
        //try{
        eventBean.setEventName(eventNameTF.getText());
        eventBean.setEventCity(eventCityTF.getText());
        eventBean.setEventAddress(eventAddressTF.getText());
        /*}catch (LengthFieldException e) {
            this.popErr.displayErrorPopup(e.getMsg());
        }*/
        eventBean.setEventMusicGenre(musicGenreBox.getValue());
        eventBean.setEventDate(datePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        eventBean.setEventTime(eventHourTF.getText(), eventMinutesTF.getText());
        eventBean.setEventPicData(eventPicData);
        eventBean.setEventOrganizer(LoggedUser.getUserName());
        try {
            if (facade.addEvent(eventBean)) {
                //alerts
                alert.displayAlertPopup(Alerts.INFORMATION, "Event added successfully");
                changeGUI(event, "HomeOrg.fxml");
            } else {
                alert.displayAlertPopup(Alerts.WARNING, "Event adding procedure failed. Please retry...");
            }
        }catch(Exception e){
            alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete event adding procedure!");
        }


    }

}
