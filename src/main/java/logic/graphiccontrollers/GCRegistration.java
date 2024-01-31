package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

//import logic.beans.BCity;
//import logic.beans.BProvince;
import logic.beans.BUserData;
import logic.controllers.CRegistration;
import logic.utils.Alerts;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;



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
    private ToggleGroup group;


    private AlertPopup alert;
    private EssentialGUI gui;
    private CRegistration registrationCtrl;
    private BUserData dataBean;

//    private BCity cityBean;
//
//    private BProvince provinceBean;

    //da rimuovere
    //private static final Logger logger = Logger.getLogger(GCRegistration.class.getName());

    @FXML
    public void initialize() {

        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.registrationCtrl = new CRegistration();
        this.gender.getItems().addAll("Male", "Female", "Other");
        this.group = new ToggleGroup();
        this.dataBean = new BUserData();

        userRadio.setSelected(true);
        userRadio.setToggleGroup(group);
        organizerRadio.setToggleGroup(group);

//        this.registrationCtrl.fetchProvincesList();
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        loadProvincesBox();
//        ObservableList<String> provincesList = FXCollections.observableArrayList(this.provinceBean.getProvincesList());
//        provinceBox.setItems(provincesList);

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


    private void register(MouseEvent event) {
        try {
            if (userRadio.isSelected()) {
                this.dataBean.setType(this.userRadio.getText());
            } else {
                this.dataBean.setType(this.organizerRadio.getText());
            }
            if (this.registrationCtrl.registerUserControl(this.dataBean)) {
                this.alert.displayAlertPopup(Alerts.INFORMATION, "Successfully registered to NightPlan");
                this.gui.changeGUI(event, "Login.fxml");
            } else {
                this.alert.displayAlertPopup(Alerts.WARNING, "Registration procedure failed. Please retry...");
            }
        } catch (Exception e) {
            this.alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete registration!");
        }
    }

/*
    private void setupProvinceBoxListener(){
        System.out.println("listener province box avviato...");

        provinceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("ho scelto la provincia " + newValue);
            this.provinceBean.setProvinceName(newValue);
            updateCityBox();
        });
    }
    private void updateCityBox(){
        this.registrationCtrl.loadCitiesList();
        ObservableList<String> citiesList = FXCollections.observableArrayList(this.cityBean.getCitiesList());
        cityBox.getItems().clear();
        cityBox.setItems(citiesList);

    }
 */
}
