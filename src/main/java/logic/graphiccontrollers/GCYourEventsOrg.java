package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import logic.beans.BEvent;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;

import java.net.URL;
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

    @FXML
    public void initialize() {
        this.eventsBeanList = cfacade.retrieveEvents(LoggedUser.getUserType(), "YourEventsOrg.fxml");
        if (eventsBeanList!=null){
            populateUpcEventsLW();
            populateTimeAndDateLW();
            populateMusicLW();

        }

    }

    private void populateUpcEventsLW(){
        for (BEvent bEvent : eventsBeanList) {
            this.upcEventsLW.getItems().add(bEvent.getEventName());
        }
    }

    private void populateTimeAndDateLW(){
        for (int i = 0; i < eventsBeanList.size(); i++) {
            this.timeAndDateLW.getItems().add(formatTimeAndDate(eventsBeanList.get(i).getEventDate(),eventsBeanList.get(i).getEventTime()));
        }
    }

    private void populateMusicLW(){
        for (BEvent bEvent : eventsBeanList) {
            this.musicLW.getItems().add(bEvent.getEventMusicGenre());
        }
    }




}
