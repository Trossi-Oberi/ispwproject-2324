package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.time.LocalDate;

public class GCGoogleRegistration extends GCRegistration {
    @FXML
    @Override
    public void initialize() {
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.facadeController = new CFacade();
        this.gender.getItems().addAll("Male", "Female", "Other");
        this.dataBean = new BUserData(LoggedUser.getUserName()); //inizializzo il data bean con username preso da GoogleAuth

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

    @FXML
    @Override
    public void registerControl(MouseEvent event) {
        try {
            this.dataBean.setUsername(this.dataBean.getUsername()); //prendo il databean visto che ho rimosso i campi username e password
            this.dataBean.setPassword("google_account_hidden_psw");
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
        } catch (Exception e) { //vanno configurate tutte le eccezioni nel dataBean (nome troppo lungo, data non valida, etc...)
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }
}
