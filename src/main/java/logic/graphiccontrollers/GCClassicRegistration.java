package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.utils.Alerts;
import logic.utils.UserTypes;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.time.LocalDate;

public class GCClassicRegistration extends GCRegistration{

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @Override
    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
        this.gender.getItems().addAll("Male", "Female", "Other");
        this.dataBean = new BUserData();

        //Disabilita le date successive a quella odierna per il datePicker
        this.birthDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });

        this.provincesList = facadeController.getProvincesList();
        ObservableList<String> provinces = FXCollections.observableArrayList(provincesList);
        this.provinceBox.setItems(provinces);
        setupProvinceBoxListener();

        this.userRadio.setSelected(true);
        this.userRadio.setToggleGroup(group);
        this.organizerRadio.setToggleGroup(group);
    }

    @Override
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
            if (this.userRadio.isSelected()) {
                this.dataBean.setType(UserTypes.USER);
            } else {
                this.dataBean.setType(UserTypes.ORGANIZER);
            }
            register(event);
        } catch (
                Exception e) { //vanno configurate tutte le eccezioni nel dataBean (nome troppo lungo, data non valida, etc...)
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }
}
