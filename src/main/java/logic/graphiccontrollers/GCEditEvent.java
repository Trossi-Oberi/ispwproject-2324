package logic.graphiccontrollers;

import com.google.api.client.util.DateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import logic.beans.BEvent;
import logic.view.EssentialGUI;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class GCEditEvent extends EssentialGUI {

    @FXML
    private TextField eventNameTF;

    @FXML
    private ChoiceBox<String> provinceChoiceBox;

    @FXML
    private ChoiceBox<String> cityChoiceBox;
    protected ArrayList<String> provincesList = new ArrayList<>();
    protected ArrayList<String> citiesList = new ArrayList<>();

    @FXML
    private TextField eventAddressTF;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField eventHourTF;

    @FXML
    private TextField eventMinutesTF;

    @FXML
    private ChoiceBox<String> musicGenreBox;

    @FXML
    private Button fileChooserBtn;

    @FXML
    private Label pickedFileLabel;
    private BEvent eventBean;
    private byte[] eventPicData;

    public void initialize() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
    }

    @FXML
    void pickImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Immagini (*.png, *.jpg)", "*.png", "*jpg");
        fc.getExtensionFilters().addAll(imageFilter);
        File eventPicFile = fc.showOpenDialog(null);

        if (eventPicFile != null) {
            pickedFileLabel.setText(eventPicFile.getName());
        }

        //conversione da file ad array di bytes
        if (eventPicFile != null) {
            try {
                this.eventPicData = Files.readAllBytes(eventPicFile.toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    public void initPageFromBean(BEvent eventB) {
        //TODO: Aggiungere nell'event dao una entry per il path dell'immagine dell'evento, aggiornare anche il model per farlo combaciare col bean e le retrieve del DAO per restituire gli eventi con tutte le informazioni

        this.eventNameTF.setText(eventB.getEventName());

        //province e citta' choicebox
        this.provincesList = cfacade.getProvincesList();
        ObservableList<String> provinces = FXCollections.observableArrayList(provincesList);
        this.provinceChoiceBox.setItems(provinces);
        this.provinceChoiceBox.setValue(eventB.getEventProvince());
        setupProvinceBoxListener();
        this.cityChoiceBox.setValue(eventB.getEventCity());

        this.eventAddressTF.setText(eventB.getEventAddress());
        setDate(eventB.getEventDate());
        setTime(eventB.getEventTime());
        this.musicGenreBox.setValue(eventB.getEventMusicGenre());
        this.pickedFileLabel.setText(eventB.getEventPicPath());

        //salvo localmente il bean evento
        this.eventBean = eventB;
    }

    @FXML
    void editEventControl(MouseEvent event) {

    }

    @FXML
    void goToEventPageOrg(MouseEvent event) {

    }

    protected void setupProvinceBoxListener() {
        this.provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.citiesList.clear();
            this.citiesList = cfacade.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    protected void updateCityListView() {
        ObservableList<String> citiesList = FXCollections.observableArrayList(this.citiesList);
        this.cityChoiceBox.setItems(citiesList);
    }

    private void setTime(String time){
        String[] timeArr = time.split(":");
        this.eventHourTF.setText(timeArr[0]);
        this.eventMinutesTF.setText(timeArr[1]);
    }

    private void setDate(String dateFromBean){
        String date = dateFromBean;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.datePicker.setValue(LocalDate.parse(date, formatter));
    }

}
