package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import logic.beans.BEvent;
import logic.utils.LoggedUser;

import java.util.ArrayList;

public class GCHomeUser extends GCHomeGeneral {

    @FXML
    private ListView<String> eventsListView;

    @FXML
    private ListView<String> musicListView;

    @FXML
    private ListView<String> groupsListView;

    private ArrayList<BEvent> eventsList = new ArrayList<>();

    public void initialize(){
        //inserire retrive eventi della tua città all'avvio della schermata
        this.eventsList = cfacade.retrieveEvents(LoggedUser.getUserType(), "HomeUser.fxml");
        populateEventsListView();
    }

    private void populateEventsListView(){
        for (int i = 0; i < this.eventsList.size(); i++) {
            this.eventsListView.getItems().add(this.eventsList.get(i).getEventName());
        }
    }

}


