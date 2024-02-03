package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import logic.beans.BEvent;
import logic.utils.LoggedUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class GCYourEventsOrg extends GCYourEventsGeneral{

    @FXML
    private ListView<String> analyticsLW;

    @FXML
    private ListView<String> musicLW;

    @FXML
    private ListView<String> pastEventsLW;

    @FXML
    private ListView<String> timeAndDateLW;

    @FXML
    private ListView<String> upcEventsLW;
    
    private ArrayList <BEvent> eventsBeanList = new ArrayList<>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void initialize() {
        this.eventsBeanList = cfacade.retrieveEvents(LoggedUser.getUserType(), "YourEventsOrg.fxml");
        if (eventsBeanList!=null){
            populateLWs();
            /*populateTimeAndDateLW();
            populateMusicLW();
            populateAnalyticsLW();*/
        }

    }

    private void populateLWs(){

        for (BEvent bEvent : eventsBeanList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString,dateTimeFormatter);
            if(LocalDate.now().isBefore(date)){
                this.upcEventsLW.getItems().add(bEvent.getEventName());
                this.timeAndDateLW.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.musicLW.getItems().add(bEvent.getEventMusicGenre());
                
            }else{
                this.pastEventsLW.getItems().add(bEvent.getEventName());
                this.analyticsLW.getItems().add(bEvent.getEventName()+" - Analytics");
            }

        }
    }

    /*private void populateTimeAndDateLW(){
        for (BEvent bEvent : eventsBeanList) {
            String eventDateString = bEvent.getEventDate();
            if (LocalDate.now().isBefore(LocalDate.parse(eventDateString,dateTimeFormatter))) //se data odierna viene prima di data dell'evento
                this.timeAndDateLW.getItems().add(formatTimeAndDate(eventDateString, bEvent.getEventTime()));
        }
    }

    private void populateMusicLW(){
        for (BEvent bEvent : eventsBeanList){
            String eventDateString = bEvent.getEventDate();
            if (LocalDate.now().isBefore(LocalDate.parse(eventDateString,dateTimeFormatter))){  //se data odierna viene prima di data dell'evento
                musicLW.getItems().add(bEvent.getEventMusicGenre());
            } 
            
        }
    }

    private void populateAnalyticsLW(){
        for (int i = 0; i < pastEventsLW.getItems().size(); i++) {
            analyticsLW.getItems().add(pastEventsLW.getItems().get(i)+" - Analytics");
        }
    }*/




}
