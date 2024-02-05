package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import logic.beans.BEvent;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;


public class GCYourEventsOrg extends GCYourEventsGeneral{

    @FXML
    private ListView<String> analyticsLV;

    @FXML
    private ListView<String> musicLV;

    @FXML
    private ListView<String> pastEventsLV;

    @FXML
    private ListView<String> timeAndDateLV;

    @FXML
    private ListView<String> upcEventsLV;
    
    private ArrayList <BEvent> eventsBeanList = new ArrayList<>();

    private ArrayList <BEvent> upComingEventsBeans = new ArrayList<>();
    private ArrayList <BEvent> pastEventsBeans = new ArrayList<>();

    @FXML
    public void initialize() {
        this.eventsBeanList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        if (eventsBeanList!=null){
            populateLVs();
            setupEventClickListener();
        }
    }

    private void populateLVs(){

        for (BEvent bEvent : eventsBeanList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString,dateTimeFormatter);

            //problema: se evento ha stesso data di oggi viene considerato passato
            if(LocalDate.now().isBefore(date)){
                upComingEventsBeans.add(bEvent);
                this.upcEventsLV.getItems().add(bEvent.getEventName());
                this.timeAndDateLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.musicLV.getItems().add(bEvent.getEventMusicGenre());
                
            }else{
                pastEventsBeans.add(bEvent);
                this.pastEventsLV.getItems().add(bEvent.getEventName());
                this.analyticsLV.getItems().add(bEvent.getEventName()+" - Analytics");
            }

        }
    }
    private void setupEventClickListener(){
        analyticsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedEventIndex = analyticsLV.getSelectionModel().getSelectedIndex();
                // Verifica se è stato effettuato un doppio clic
                BEvent selectedEventBean = pastEventsBeans.get(selectedEventIndex);
                try {
                    onItemDoubleClick(event, selectedEventBean);
                } catch (RuntimeException e){
                    alert.displayAlertPopup(Alerts.ERROR, "FATAL ERROR, runtime exception on double click");
                }
            }
        });
        upcEventsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                alert.displayAlertPopup(Alerts.ERROR, "Not implemented :(");
            }
        });
    }

    private void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean) {
        try {
            URL loc = EssentialGUI.class.getResource("Analytics.fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if(loc != null) {
                root = loader.load();
            }
            scene = new Scene(root);
            scene.getStylesheets().add(EssentialGUI.class.getResource("application.css").toExternalForm());

            GCAnalytics analyticsGC = loader.getController();
            analyticsGC.initAnalyticsByBean(selectedEventBean);
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e){
            throw new RuntimeException(e);
        }
        nextGuiOnClick(event);
    }
}
