package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.interfaces.DoubleClickListener;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

public class GCHomeUser extends EssentialGUI implements DoubleClickListener {



    @FXML
    private ListView<String> eventsListView;

    @FXML
    private ListView<String> musicListView;

    @FXML
    private ListView<String> groupsListView;

    private ArrayList<BEvent> eventsList = new ArrayList<>();
    private ArrayList<BEvent> upcomingEventsList = new ArrayList<>();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void initialize(){
        eventsList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        populateEventsListView();
        setupEventClickListener();
    }
    private void populateEventsListView() {
        for (BEvent bEvent : eventsList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, dateTimeFormatter);
            if(LocalDate.now().isBefore(date)) {
                this.eventsListView.getItems().add(bEvent.getEventName());
                this.musicListView.getItems().add(bEvent.getEventMusicGenre());
                this.groupsListView.getItems().add("No group");
                this.upcomingEventsList.add(bEvent);
            }
        }
    }

    @Override
    public void setupEventClickListener(){
        eventsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedEventIndex = eventsListView.getSelectionModel().getSelectedIndex();
                // Verifica se è stato effettuato un doppio clic
                BEvent selectedEventBean = upcomingEventsList.get(selectedEventIndex);
                try {
                    onItemDoubleClick(event, selectedEventBean, "EventPageUser.fxml");
                } catch (RuntimeException e){
                    alert.displayAlertPopup(Alerts.ERROR, "FATAL ERROR, runtime exception on double click");
                }
            }
        });
        groupsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                alert.displayAlertPopup(Alerts.ERROR, "Not implemented :(");
            }
        });
    }

    @Override
    public void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean, String fxmlpage) {
            try {
                URL loc = EssentialGUI.class.getResource(fxmlpage);
                FXMLLoader loader = new FXMLLoader(loc);
                Parent root = null;
                if(loc != null) {
                    root = loader.load();
                }
                scene = new Scene(root);
                scene.getStylesheets().add(EssentialGUI.class.getResource("application.css").toExternalForm());

                GCEventPageUser eventPageGC = loader.getController();
                eventPageGC.initEventFromBean(selectedEventBean);
            } catch (IOException | NullPointerException e) {
                logger.log(Level.SEVERE, "Cannot load scene\n", e);
            } catch (RuntimeException e){
                throw new RuntimeException(e);
            }
            nextGuiOnClick(event);
    }

}


