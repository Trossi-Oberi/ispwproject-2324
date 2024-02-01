package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import logic.beans.BEvent;
import logic.utils.Alerts;
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
        populateEventsListViews();
        setupEventClickListener();
    }
    private void populateEventsListViews() {
        for (int i = 0; i < this.eventsList.size(); i++) {
            this.eventsListView.getItems().add(this.eventsList.get(i).getEventName());
            this.musicListView.getItems().add(this.eventsList.get(i).getEventMusicGenre());
            this.groupsListView.getItems().add("No group");
        }
    }

    private void setupEventClickListener(){
        eventsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                onItemDoubleClick(eventsListView.getSelectionModel().getSelectedItem());
            }
        });
        groupsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                goToSettings(event);
            }
        });
    }

    private void onItemDoubleClick(String selectedItem) {
        // Esegui la tua funzione con l'elemento selezionato
        //System.out.println("Evento cliccato: " + selectedItem);
        alert.displayAlertPopup(Alerts.CONFIRMATION, "Evento selezionato: " + selectedItem);
    }
}


