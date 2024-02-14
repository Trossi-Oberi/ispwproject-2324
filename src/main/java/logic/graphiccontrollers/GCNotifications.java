package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import logic.model.NotificationMessage;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;
import logic.view.EssentialGUI;

import java.util.ArrayList;
import java.util.logging.Level;

public class GCNotifications extends EssentialGUI {
    @FXML
    private ListView<String> notificationsLV;

    private class DeleteButtonCell extends ListCell<String> {
        private HBox hbox;
        private Label label;
        private Region spacer;
        private Button deleteButton;

        public DeleteButtonCell(ListView<String> param) {
            hbox = new HBox();
            label = new Label();
            HBox.setHgrow(label, Priority.ALWAYS);
            spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            deleteButton = new Button("Delete");
            deleteButton.setOnAction(event -> {
                getListView().getItems().remove(getItem());
                alert.displayAlertPopup(Alerts.INFORMATION, "Removed notification successfully");
            });

            hbox.getChildren().addAll(label, spacer, deleteButton);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(5);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                label.setText(item);
                setGraphic(hbox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    @FXML
    public void initialize() {

        //TODO: fare la retrieve di tutte le notifiche dal db, caso user: eventAdded, caso organizer: userParticipation
        //Imposta la cell factory personalizzata
        notificationsLV.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new DeleteButtonCell(param);
            }
        });

        switch (LoggedUser.getUserType()) {
            case USER:
                ArrayList<NotificationMessage> notificationsList = cfacade.retrieveNotifications(LoggedUser.getUserID());
                populateNotificationsLV(notificationsList);
                break;
            case ORGANIZER:
                //TODO: retrieve notifiche per gli organizer
                break;
        }

/*        IMPORTANTE: massima lunghezza di una notifica deve essere di 118 caratteri per evitare sbilanciamenti
        ObservableList<String> items = FXCollections.observableArrayList(
                "Notify 1",
                "Notify 2",
                "Notify 3"
        );
        notificationsLV.setItems(items);*/

    }

    private void populateNotificationsLV(ArrayList<NotificationMessage> notificationsList) {
        try {
            for (int i = 0; i < notificationsList.size(); i++) {
                if (notificationsList.get(i).getMessageType() == MessageTypes.EventAdded) {
                    notificationsLV.getItems().add("New event called " + cfacade.getEventNameByEventID(notificationsList.get(i).getEventID()) + "in your city!");
                }
            }

        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Invalid notification string value");
        }
    }

    public void addNewNotifications() {
        String notify = "null; not implemented";
//        notify = DA IMPLEMENTARE, questo metodo verrà chiamato dal singolo observer (utente) chiamato a sua volta dal subject dell'organizer
        try {
            notificationsLV.getItems().add(notify);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Invalid notification string value");
        }
    }


}
