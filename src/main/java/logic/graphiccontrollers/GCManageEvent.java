package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import logic.beans.BEvent;
import logic.utils.MusicGenres;
import logic.view.EssentialGUI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;

public class GCManageEvent extends EssentialGUI {
    @FXML
    protected DatePicker datePicker;
    @FXML
    protected TextField eventAddressTF;
    @FXML
    protected TextField eventNameTF;
    @FXML
    protected TextField eventHourTF;

    @FXML
    protected TextField eventMinutesTF;
    @FXML
    protected Button fileChooserBtn;
    @FXML
    protected Label pickedFileLabel;
    @FXML
    protected ChoiceBox<String> musicGenreBox;
    @FXML
    protected ChoiceBox<String> cityChoiceBox;
    @FXML
    protected ChoiceBox<String> provinceChoiceBox;

    protected BEvent eventBean;
    protected byte[] eventPicData;
    protected ArrayList<String> provincesList = new ArrayList<>();
    protected ObservableList<String> provincesObsList;
    protected ArrayList<String> citiesList = new ArrayList<>();
    protected ObservableList<String> citiesObsList;
    protected final ObservableList<String> musicGenresList = FXCollections.observableArrayList(MusicGenres.MUSIC_GENRES);

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

    protected void setupProvinceBoxListener() {
        this.provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.citiesList.clear();
            this.citiesList = cfacade.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    protected void updateCityListView() {
        citiesObsList = FXCollections.observableArrayList(this.citiesList);
        this.cityChoiceBox.setItems(citiesObsList);
    }


}
