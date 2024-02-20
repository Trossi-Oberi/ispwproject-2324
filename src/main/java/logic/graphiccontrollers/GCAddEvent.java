package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.DateCell;
import javafx.fxml.FXML;
import logic.utils.Alerts;
import logic.utils.MusicGenres;
import java.time.LocalDate;
import logic.beans.BEvent;


public class GCAddEvent extends GCManageEvent {

    public GCAddEvent() {
        eventBean = new BEvent();
    }

    @FXML
    public void initialize() {
        //Disabilita le date precedenti a quella odierna per il datePicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });

        if(provincesList.isEmpty()){
            provincesList = cfacade.getProvincesList();
            provincesObsList = FXCollections.observableArrayList(provincesList);
        }

        this.provinceChoiceBox.setItems(provincesObsList);
        setupProvinceBoxListener();

        musicGenreBox.setValue(MusicGenres.MUSIC_GENRES[0]);
        musicGenreBox.setItems(musicGenresList);
    }

    @FXML
        //azioni da eseguire quando clicko pulsante "CONFIRM"
    void addEventControl(MouseEvent event) {
        setEventBean(this.eventBean);
        try {
            if (cfacade.addEvent(eventBean)) {
                //alerts
                alert.displayAlertPopup(Alerts.INFORMATION, "Event added successfully");
                changeGUI(event, "HomeOrg.fxml");
            } else {
                alert.displayAlertPopup(Alerts.WARNING, "Event adding procedure failed. Please retry...");
            }
        } catch (Exception e) {
            alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete event adding procedure!");
        }
    }


    //TODO: fare gestione eccezioni alla creazione evento
}
