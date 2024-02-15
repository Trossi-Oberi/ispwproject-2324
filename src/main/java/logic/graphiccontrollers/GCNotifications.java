package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import logic.beans.BNotification;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.utils.NotificationTypes;
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
                //TODO: Implementare cancellazione notifiche
                //cfacade.deleteNotification();
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

        BNotification notiBean = new BNotification();
        //Imposta la cell factory personalizzata
        notificationsLV.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new DeleteButtonCell(param);
            }
        });
        ArrayList<BNotification> notificationsList = cfacade.retrieveNotifications(LoggedUser.getUserID());
        populateNotificationsLV(notificationsList);

    }

    private void populateNotificationsLV(ArrayList<BNotification> notificationsList) {
        try {
            for (int i = 0; i < notificationsList.size(); i++) {
                if (notificationsList.get(i).getMessageType() == NotificationTypes.EventAdded) {
                    notificationsLV.getItems().add("New event called " + cfacade.getEventNameByEventID(notificationsList.get(i).getEventID()) + " in your city!");
                } else if (notificationsList.get(i).getMessageType() == NotificationTypes.UserEventParticipation) {
                    notificationsLV.getItems().add("New user " + cfacade.getUsernameByID(notificationsList.get(i).getClientID()) + " participating to your event " + cfacade.getEventNameByEventID(notificationsList.get(i).getEventID()));
                }
            }

        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Invalid notification string value");
        }
    }


    //AGGIORNAMENTO DINAMICO NOTIFICHE (DA FARE?)
   /* public void addNewNotifications() {
        String notify = "null; not implemented";
//        notify = DA IMPLEMENTARE, questo metodo verrà chiamato dal singolo observer (utente) chiamato a sua volta dal subject dell'organizer
        try {
            notificationsLV.getItems().add(notify);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Invalid notification string value");
        }
    }*/


}
