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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

public class GCYourEventsUser extends GCYourEventsGeneral {
    @FXML
    private ListView<String> pastEventsLV;

    @FXML
    private ListView<String> pastMusicLV;

    @FXML
    private ListView<String> pastTimeLV;

    @FXML
    private ListView<String> upComingEventsLV;

    @FXML
    private ListView<String> upComingTimeLV;

    @FXML
    private ListView<String> upcomingMusicLV;

    private ArrayList <BEvent> eventsParticipationList = new ArrayList<>();
    private ArrayList <BEvent> upcEventsBeanList = new ArrayList<>();
    private ArrayList <BEvent> pastEventsBeanList = new ArrayList<>();


    @FXML
    public void initialize() {
        this.eventsParticipationList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        if (eventsParticipationList !=null){
            populateLVs();
            //setupEventClickListener();
        }
    }

    private void populateLVs(){

        for (BEvent bEvent : eventsParticipationList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString,dateTimeFormatter);

            //problema: se evento ha stesso data di oggi viene considerato passato
            if(LocalDate.now().isBefore(date)){
                upcEventsBeanList.add(bEvent);
                this.upComingEventsLV.getItems().add(bEvent.getEventName());
                this.upComingTimeLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.upcomingMusicLV.getItems().add(bEvent.getEventMusicGenre());

            }else{
                pastEventsBeanList.add(bEvent);
                this.pastEventsLV.getItems().add(bEvent.getEventName());
                this.pastTimeLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.pastMusicLV.getItems().add(bEvent.getEventMusicGenre());
            }

        }
    }

}
