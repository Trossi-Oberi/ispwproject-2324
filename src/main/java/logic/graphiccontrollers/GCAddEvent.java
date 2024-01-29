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
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import logic.utils.MusicGenres;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import logic.beans.BEvent;
import logic.controllers.CFacade;


public class GCAddEvent{

    ObservableList<String> musicGenresList = FXCollections.observableArrayList(MusicGenres.musicgenresarr);
    private EssentialGUI gui;
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
    private File eventPicFile;

    public GCAddEvent(){
        eventBean = new BEvent();
    }
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
    void getDate(ActionEvent event) {

        LocalDate myDate = datePicker.getValue();
        String myFormattedDate = myDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

    @FXML
    void pickImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        ExtensionFilter imageFilter = new ExtensionFilter("Immagini (*.png, *.jpg)", "*.png", "*jpg");
        fc.getExtensionFilters().addAll(imageFilter);
        eventPicFile = fc.showOpenDialog(null);

        if (eventPicFile!=null){
            pickedFileLabel.setText(eventPicFile.getName());
        }

    }

    @FXML //azioni da eseguire quando clicko pulsante "CONFIRM"
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
        eventBean.setEventTime(eventHourTF.getText(),eventMinutesTF.getText());
        eventBean.setEventPicData(eventPicFile);
        eventBean.setEventOrganizer(LoggedUser.getUserName());
        facade.addEvent(eventBean);
        System.out.println("evento aggiunto correttamente");
        gui.changeGUI(event, "HomeOrg.fxml");




    }

}
