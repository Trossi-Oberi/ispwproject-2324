package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;

public class GCEditEvent extends GCManageEvent {

    BEvent oldBean;

    @FXML
    public void initialize() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
    }


    public void initPageFromBean(BEvent eventB) {
        //TODO: Aggiungere nell'event dao una entry per il path dell'immagine dell'evento, aggiornare anche il model per farlo combaciare col bean e le retrieve del DAO per restituire gli eventi con tutte le informazioni
        this.oldBean = eventB;
        this.eventNameTF.setText(eventB.getEventName());

        //province e citta' choicebox
        if (provincesList.isEmpty()) {
            provincesList = cfacade.getProvincesList();
            provincesObsList = FXCollections.observableArrayList(provincesList);
        }
        this.provinceChoiceBox.setItems(provincesObsList);
        this.provinceChoiceBox.setValue(eventB.getEventProvince()); //la provincia qui risulta null perche' nel bean non viene settata
        setupProvinceBoxListener();
        this.cityChoiceBox.setValue(eventB.getEventCity());

        this.eventAddressTF.setText(eventB.getEventAddress());
        setDate(eventB.getEventDate());
        setTime(eventB.getEventTime());
        this.musicGenreBox.setValue(eventB.getEventMusicGenre());
        this.pickedFileLabel.setText(eventB.getEventPicPath());

        //salvo localmente il bean evento
        this.eventBean = eventB;
    }

    @FXML
    void editEventControl(MouseEvent event) {
        //dopo che clicko conferma voglio essere riportato alla pagina dell'evento ma con i dati aggiornati
    }

    @FXML
    void cancelButtonControl(MouseEvent event) {
        //devo tornare all'eventpage corretta, quindi devo ri-inizializzarla di nuovo col bean "vecchio"
        goToPreviousEventPage(event, oldBean);
    }

    private void setTime(String time) {
        String[] timeArr = time.split(":");
        this.eventHourTF.setText(timeArr[0]);
        this.eventMinutesTF.setText(timeArr[1]);
    }

    private void setDate(String dateFromBean) {
        String date = dateFromBean;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.datePicker.setValue(LocalDate.parse(date, formatter));
    }

    private void goToPreviousEventPage(MouseEvent event, BEvent prevEventBean) {
        try {
            URL loc = EssentialGUI.class.getResource("EventPageOrg.fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            GCEventPageOrg eventPageOrgGC = loader.getController();
            eventPageOrgGC.initEventFromBean(prevEventBean, this.getClass().getSimpleName());
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException |
                 NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (
                RuntimeException e) {
            throw new RuntimeException(e);
        }

        nextGuiOnClick(event);
    }

}
