package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.util.ArrayList;

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

    protected ArrayList<String> provincesList = new ArrayList<>();
    protected ArrayList<String> citiesList = new ArrayList<>();

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

    protected void register(MouseEvent event) {
        try {
            if (this.facadeController.registerUser(this.dataBean)) {
                this.alert.displayAlertPopup(Alerts.INFORMATION, "Successfully registered to NightPlan");
                this.gui.changeGUI(event, "Login.fxml");
            } else {
                this.alert.displayAlertPopup(Alerts.WARNING, "Registration procedure failed. Please retry...");
            }
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
        ObservableList<String> citiesList = FXCollections.observableArrayList(this.citiesList);
        this.cityBox.setItems(citiesList);
    }
}
