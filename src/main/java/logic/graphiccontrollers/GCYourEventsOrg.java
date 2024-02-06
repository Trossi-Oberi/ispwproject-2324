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

    private ArrayList <BEvent> upcEventsBeanList = new ArrayList<>();
    private ArrayList <BEvent> pastEventsBeanList = new ArrayList<>();

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
                upcEventsBeanList.add(bEvent);
                this.upcEventsLV.getItems().add(bEvent.getEventName());
                this.timeAndDateLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.musicLV.getItems().add(bEvent.getEventMusicGenre());
                
            }else{
                pastEventsBeanList.add(bEvent);
                this.pastEventsLV.getItems().add(bEvent.getEventName());
                this.analyticsLV.getItems().add(bEvent.getEventName()+" - Analytics");
            }

        }
    }
    @Override
    public void setupEventClickListener(){
        analyticsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BEvent selectedEventBean = getBeanFromListView(analyticsLV,pastEventsBeanList);
                try {
                    onItemDoubleClick(event, selectedEventBean, "Analytics.fxml");
                } catch (RuntimeException e){
                    alert.displayAlertPopup(Alerts.ERROR, "FATAL ERROR, runtime exception on double click");
                }
            }
        });
        upcEventsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                BEvent selectedEventBean = getBeanFromListView(upcEventsLV,upcEventsBeanList);
                try {
                    onItemDoubleClick(event, selectedEventBean, "EventPageOrg.fxml");
                } catch (RuntimeException e){
                    System.out.println("Errore "+e.getMessage());
//                    alert.displayAlertPopup(Alerts.ERROR, "FATAL ERROR, runtime exception on double click");
                }
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
            if (fxmlpage.equals("Analytics.fxml")){
                GCAnalytics analyticsGC = loader.getController();
                analyticsGC.initAnalyticsByBean(selectedEventBean);
                analyticsGC.initParticipantsInfo();
            }else{
                GCEventPageOrg eventPageOrgGC = loader.getController();
                eventPageOrgGC.initEventFromBean(selectedEventBean);
            }


            scene = new Scene(root);
            scene.getStylesheets().add(EssentialGUI.class.getResource("application.css").toExternalForm());


        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e){
            throw new RuntimeException(e);
        }
        nextGuiOnClick(event);
    }

    private BEvent getBeanFromListView(ListView<String> lv, ArrayList<BEvent> beansArray){
        int selectedEventIndex = lv.getSelectionModel().getSelectedIndex();
        return beansArray.get(selectedEventIndex);
    }





}
