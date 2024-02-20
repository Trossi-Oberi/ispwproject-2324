package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.exceptions.UsernameAlreadyTaken;
import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class GCRegistration {

    @FXML
    protected DatePicker birthDate;

    @FXML
    protected ChoiceBox<String> cityBox;

    @FXML
    protected TextField emailField;

    @FXML
    protected TextField firstNameField;

    @FXML
    protected ChoiceBox<String> gender;

    @FXML
    protected TextField lastNameField;

    @FXML
    protected RadioButton organizerRadio;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected ChoiceBox<String> provinceBox;

    @FXML
    protected Button registerButton;

    @FXML
    protected Button returnLogin;

    @FXML
    protected RadioButton userRadio;
    @FXML
    protected final ToggleGroup group = new ToggleGroup();

    protected List<String> provincesList = new ArrayList<>();
    protected List<String> citiesList = new ArrayList<>();

    protected AlertPopup alert;
    protected EssentialGUI gui;
    protected CFacade facadeController;
    protected BUserData dataBean;

    @FXML
    public abstract void initialize();

    @FXML
    public abstract void registerControl(MouseEvent event);

    @FXML
    public void returnBack(MouseEvent event) {
        gui.changeGUI(event, "Login.fxml");
    }

    protected void initRegistration(){
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
        this.gender.getItems().addAll("Male", "Female", "Other");

        this.birthDate.setValue(LocalDate.of(2005, 1, 1));
        this.birthDate.getEditor().setDisable(true);
        this.birthDate.getEditor().setOpacity(1);
        //Disabilita le date successive a quella odierna per il datePicker
        this.birthDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });

        this.provincesList = facadeController.getProvincesList();
        ObservableList<String> provincesList = FXCollections.observableArrayList(this.provincesList);
        this.provinceBox.setItems(provincesList);
        setupProvinceBoxListener();

        this.userRadio.setSelected(true);
        this.userRadio.setToggleGroup(group);
        this.organizerRadio.setToggleGroup(group);
    }

    protected void register(MouseEvent event) {
        try {
            if (this.facadeController.registerUser(this.dataBean)) {
                this.alert.displayAlertPopup(Alerts.INFORMATION, "Successfully registered to NightPlan");
                this.gui.changeGUI(event, "Login.fxml");
            } else {
                this.alert.displayAlertPopup(Alerts.WARNING, "Registration procedure failed. Please retry...");
            }
        } catch (UsernameAlreadyTaken e){
            this.alert.displayAlertPopup(Alerts.WARNING, "Username " + e.getUsername() + " is already taken! Change it and retry...");
        } catch (RuntimeException e) {
            this.alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete registration! " + e.getMessage());
        }
    }

    protected void setupProvinceBoxListener() {
        this.provinceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.citiesList.clear();
            this.citiesList = this.facadeController.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    protected void updateCityListView() {
        ObservableList<String> citiesListObs = FXCollections.observableArrayList(this.citiesList);
        this.cityBox.setItems(citiesListObs);
    }
}
