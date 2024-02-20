package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.Alerts;
import logic.utils.UserTypes;

public class GCClassicRegistration extends GCRegistration{
    @Override
    @FXML
    public void initialize() {
        initRegistration();
        this.dataBean = new BUserData();
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
            this.dataBean.setProvince(this.provinceBox.getValue());
            this.dataBean.setCity(this.cityBox.getValue());
            if (this.userRadio.isSelected()) {
                this.dataBean.setType(UserTypes.USER);
            } else {
                this.dataBean.setType(UserTypes.ORGANIZER);
            }
            register(event);
        } catch (InvalidValueException | TextTooLongException e) {
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }
}
