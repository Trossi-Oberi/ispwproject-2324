package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCRegistration {

    @FXML
    private DatePicker birthDate;

    @FXML
    private ChoiceBox<String> cityBox;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private ChoiceBox<String> gender;

    @FXML
    private TextField lastNameField;

    @FXML
    private RadioButton organizerRadio;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<String> provinceBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button returnLogin;

    @FXML
    private RadioButton userRadio;

    @FXML
    private final ToggleGroup group = new ToggleGroup();

    private ArrayList<String> provincesList = new ArrayList<>();
    private ArrayList<String> citiesList = new ArrayList<>();

    private AlertPopup alert;
    private EssentialGUI gui;
    private CFacade facadeController;
    private BUserData dataBean;
    //da rimuovere
    //private static final Logger logger = Logger.getLogger(GCRegistration.class.getName());

    @FXML
    public void initialize() {

        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
        this.gender.getItems().addAll("Male", "Female", "Other");
        //this.group = new ToggleGroup();
        this.dataBean = new BUserData();

        this.provincesList = facadeController.getProvincesList();
        ObservableList<String> provincesList = FXCollections.observableArrayList(this.provincesList);
        this.provinceBox.setItems(provincesList);
        setupProvinceBoxListener();

        userRadio.setSelected(true);
        userRadio.setToggleGroup(group);
        organizerRadio.setToggleGroup(group);
    }

    @FXML
    public void registerControl(MouseEvent event) {
        try {
            this.dataBean.setUsername(this.emailField.getText());
            this.dataBean.setPassword(this.passwordField.getText());
            this.dataBean.setFirstName(this.firstNameField.getText());
            this.dataBean.setLastName(this.lastNameField.getText());
            this.dataBean.setGender(this.gender.getValue());
            this.dataBean.setBirthDate(this.birthDate.getValue());
            this.dataBean.setCity(this.cityBox.getValue());
            register(event);
        } catch (
                Exception e) { //vanno configurate tutte le eccezioni nel dataBean (nome troppo lungo, data non valida, etc...)
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }

    @FXML
    public void returnBack(MouseEvent event) {
        gui.changeGUI(event, "Login.fxml");
    }

    @FXML
    void loadProvinceBox(MouseEvent event) {


    }

    private void register(MouseEvent event) {
        try {
            if (userRadio.isSelected()) {
                this.dataBean.setType(this.userRadio.getText());
            } else {
                this.dataBean.setType(this.organizerRadio.getText());
            }
            if (facadeController.registerUser(this.dataBean)) {
                this.alert.displayAlertPopup(Alerts.INFORMATION, "Successfully registered to NightPlan");
                this.gui.changeGUI(event, "Login.fxml");
            } else {
                this.alert.displayAlertPopup(Alerts.WARNING, "Registration procedure failed. Please retry...");
            }
        } catch (Exception e) {
            this.alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete registration!");
        }
    }

    private void setupProvinceBoxListener(){
        this.provinceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.citiesList.clear();
            this.citiesList = facadeController.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }
    private void updateCityListView(){
        ObservableList<String> citiesList = FXCollections.observableArrayList(this.citiesList);
        this.cityBox.setItems(citiesList);
    }

}
