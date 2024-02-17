package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import logic.beans.BGroupMessage;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCGroupChat extends EssentialGUI {

    @FXML
    private ListView<BGroupMessage> chatMessagesLV;

    @FXML
    private Button goBackBtn;

    @FXML
    private Label groupName;

    @FXML
    private Button leaveGroupBtn;

    @FXML
    private TextField messageTextField;

    @FXML
    private Button sendBtn;
    private Integer groupID;

    private ArrayList<BGroupMessage> messages = new ArrayList<>();

    @FXML
    void leaveGroup(MouseEvent event) {
        //query al database per uscire dal gruppo
        //anche notifica al server che sono uscito (rimuovere observer da hashmap)
    }

    @FXML
    void sendMessage(MouseEvent event) {
        //scrive messaggio sul database e lo gira al server
        if(!messageTextField.getText().isEmpty() && cfacade.sendMessageToGroup(this.groupID, messageTextField.getText())){
            messageTextField.clear();
        }else{
            alert.displayAlertPopup(Alerts.ERROR, "Error while sending message");
        }

    }

    public void initGroupChat(Integer groupID) {
        this.groupID = groupID;
        messages = cfacade.retrieveGroupChat(groupID);
        setupChatTextField(messageTextField);
        setupChatLV(chatMessagesLV);
        populateChatLV(messages);
    }

    private void setupChatTextField(TextField field){
        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !field.getText().isEmpty()) {
                sendMessage(null);
            }
        });
    }

    private void setupChatLV(ListView<BGroupMessage> chatMessagesLV) {
//        chatMessagesLV.setMouseTransparent(true); // Imposta la ListView non cliccabile
        chatMessagesLV.setFocusTraversable(false); // Disabilita la selezione degli elementi
        chatMessagesLV.setOrientation(Orientation.VERTICAL);
        chatMessagesLV.setCellFactory(param -> new ListCell<BGroupMessage>() {
            @Override
            protected void updateItem(BGroupMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(createMessageNode(item));
                }
            }
        });
    }

    private void populateChatLV(ArrayList<BGroupMessage> messages) {
        for (BGroupMessage message : messages){
            chatMessagesLV.getItems().add(message);
        }
        chatMessagesLV.scrollTo(chatMessagesLV.getItems().size() - 1);
    }

    // Metodo per allineare a destra o sinistra
    private HBox createMessageNode(BGroupMessage message) {
        HBox hbox = new HBox();
        Label messageLabel = new Label();
        boolean sentByMe = LoggedUser.getUserID() == message.getSenderID();
        String senderName;
        if (sentByMe){
            senderName = "Me";
            messageLabel.setStyle("-fx-background-color: lightblue; -fx-background-radius: 8px;");
            hbox.setAlignment(Pos.CENTER_RIGHT);
        }else{
            senderName = cfacade.getUsernameByID(message.getSenderID());
            messageLabel.setStyle("-fx-background-color: lavender; -fx-background-radius: 8px;");
            hbox.setAlignment(Pos.CENTER_LEFT);
        }
        messageLabel.setText(senderName+": "+message.getMessage());
        messageLabel.setPadding(new Insets(5));

        hbox.getChildren().add(messageLabel);
        hbox.setPadding(new Insets(5));

        return hbox;
    }



}
