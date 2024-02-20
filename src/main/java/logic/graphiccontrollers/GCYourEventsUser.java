package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import logic.beans.BEvent;
import logic.beans.BGroup;
import logic.exceptions.GroupAlreadyCreated;
import logic.interfaces.DoubleClickListener;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class GCYourEventsUser extends GCYourEventsGeneral implements DoubleClickListener {
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
    @FXML
    private ListView<BGroup> groupsLV;

    private List<BEvent> eventsParticipationList = new ArrayList<>();
    private ArrayList<BEvent> upComingEventsBeans = new ArrayList<>();
    private ArrayList<BEvent> pastEventsBeans = new ArrayList<>();
    private ArrayList<BGroup> groupsBeans = new ArrayList<>();

    // Classe per personalizzare la visualizzazione delle celle nella ListView
    private class GroupListCell extends ListCell<BGroup> {
        private Label groupName = new Label();

        @Override
        protected void updateItem(BGroup item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {

                // Personalizzazione del pulsante in base allo stato del gruppo e dell'appartenenza
                boolean res = cfacade.userInGroup(LoggedUser.getUserID(), item.getGroupID());
                Button groupButton = new Button();
                groupName.setFont(new Font("System", 12));
                groupButton.setFont(new Font("System", 9));
                if (item.getGroupID() != null && res) {
                    groupName.setText(item.getGroupName());

                    groupButton.setText("Group chat");
                    groupButton.setOnMouseClicked(event -> {
                        //OPEN GROUP CHAT
                        setupGroupChat(event, groupsBeans.get(getIndex()).getGroupID());

                    });
                } else if (item.getGroupID() != null && !res) {
                    groupName.setText(item.getGroupName());
                    groupButton.setText("Join group");
                    groupButton.setOnMouseClicked(event -> {
                        //JOIN GROUP
                        if(cfacade.joinGroup(groupsBeans.get(getIndex()).getGroupID())) {
                            changeGUI(event, "YourEventsUser.fxml");
                        } else {
                            alert.displayAlertPopup(Alerts.ERROR, "Error while joining group");
                        }
                    });
                } else {
                    groupName.setText("No group");
                    groupButton.setText("Create group");
                    groupButton.setOnMouseClicked(event -> {
                        //CREATE GROUP (AND JOIN)
                        String groupName = askUserForGroupName();
                        try {
                            if(cfacade.createGroup(groupName, upComingEventsBeans.get(getIndex()).getEventID())){
                                System.out.println("Successfully created and joined new group");
                                changeGUI(event,"YourEventsUser.fxml");
                            } else {
                                alert.displayAlertPopup(Alerts.ERROR, "Error while joining group");
                            }
                        } catch (GroupAlreadyCreated e) {
                            alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
                            changeGUI(event,"YourEventsUser.fxml");
                        }
                    });
                }

                HBox hbox = new HBox();
                HBox.setHgrow(groupName, Priority.ALWAYS);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setSpacing(5);
                hbox.getChildren().addAll(groupName, spacer, groupButton);
                setGraphic(hbox);
            }
        }
        private String askUserForGroupName(){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Insert group name");
            dialog.setHeaderText("Group name");
            dialog.setContentText("Group name:");

            // Visualizzazione del dialogo e attesa della chiusura
            Optional<String> result = dialog.showAndWait();

            // Salvataggio del testo inserito dall'utente in una variabile Stringa
            return result.orElse("");
        }
    }


    @FXML
    public void initialize() {
        this.eventsParticipationList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        if (eventsParticipationList != null) {
            setCellFactory(upComingEventsLV);
            setCellFactory(upComingTimeLV);
            setCellFactory(upcomingMusicLV);
            setCellFactory(pastEventsLV);
            setCellFactory(pastTimeLV);
            setCellFactory(pastMusicLV);
            populateLVs();
            setupEventClickListener();
        }
    }

    private void setCellFactory(ListView<String> lv) {
        lv.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    // Imposta la dimensione del testo della cella
                    setAlignment(Pos.CENTER);
                    setFont(new Font("System", 13));
                }
            }
        });
    }

    private void populateLVs() {

        for (BEvent bEvent : eventsParticipationList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, dateTimeFormatter);

            if (LocalDate.now().minusDays(1).isBefore(date)) {
                upComingEventsBeans.add(bEvent);
                BGroup group = cfacade.getGroupByEventID(bEvent.getEventID());
                groupsBeans.add(group);
                this.upComingEventsLV.getItems().add(bEvent.getEventName());

                //GROUP
                groupsLV.setCellFactory(param -> new GroupListCell());
                groupsLV.getItems().add(group);

                this.upComingTimeLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.upcomingMusicLV.getItems().add(bEvent.getEventMusicGenre());

            } else {
                pastEventsBeans.add(bEvent);
                this.pastEventsLV.getItems().add(bEvent.getEventName());
                this.pastTimeLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.pastMusicLV.getItems().add(bEvent.getEventMusicGenre());
            }

        }
    }

    @Override
    public void setupEventClickListener() {
        upComingEventsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BEvent selectedEventBean = getBeanFromListView(upComingEventsLV, upComingEventsBeans);
                try {
                    if (selectedEventBean != null) {
                        onItemDoubleClick(event, selectedEventBean, "EventPageUser.fxml");
                    }
                } catch (RuntimeException e) {
                    alert.displayAlertPopup(Alerts.ERROR, "Fatal: " + e.getMessage());
                }
            }
        });
        pastEventsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BEvent selectedEventBean = getBeanFromListView(pastEventsLV, pastEventsBeans);
                try {
                    if (selectedEventBean != null) {
                        onItemDoubleClick(event, selectedEventBean, "EventPageUser.fxml");
                    }
                } catch (RuntimeException e) {
                    alert.displayAlertPopup(Alerts.ERROR, "Fatal: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean, String fxmlpage) throws RuntimeException {
        try {
            URL loc = EssentialGUI.class.getResource(fxmlpage);
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());

            GCEventPageUser eventPageUserGC = loader.getController();
            eventPageUserGC.initEventFromBean(selectedEventBean, this.getClass().getSimpleName());
            eventPageUserGC.initEventPageButton();
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        nextGuiOnClick(event);
    }

    private void setupGroupChat(MouseEvent event, int groupID){
        try {
            URL loc = EssentialGUI.class.getResource("GroupChat.fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());

            GCGroupChat groupChatGC = loader.getController();
            groupChatGC.initGroupChat(groupID);
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        nextGuiOnClick(event);
    }

}
