package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCEditCity extends EssentialGUI {

    @FXML
    private ChoiceBox<String> cityChoiceBox;

    @FXML
    private ChoiceBox<String> provinceChoiceBox;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button goBackBtn;

    @FXML
    private Label pickedFileLabel;

    private ArrayList<String> provincesList = new ArrayList<>();
    private ObservableList<String> provincesObsList;
    private ArrayList<String> citiesList = new ArrayList<>();
    private ObservableList<String> citiesObsList;


    @FXML
    void initialize(){
        //province e citta' choicebox
        if (provincesList.isEmpty()) {
            provincesList = cfacade.getProvincesList();
            provincesObsList = FXCollections.observableArrayList(provincesList);
        }
        this.provinceChoiceBox.setItems(provincesObsList);
        this.provinceChoiceBox.setValue(LoggedUser.getProvince());
        this.cityChoiceBox.setValue(LoggedUser.getCity());
        setupProvinceBoxListener();
    }

    @FXML
    void changeCityControl(MouseEvent event) {

    }


    private void setupProvinceBoxListener() {
        provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            citiesList.clear();
            citiesList = cfacade.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    private void updateCityListView() {
        citiesObsList = FXCollections.observableArrayList(citiesList);
        cityChoiceBox.setItems(citiesObsList);
    }


}
