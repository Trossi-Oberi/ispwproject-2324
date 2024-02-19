package logic.graphiccontrollers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import logic.beans.BMessage;
import logic.utils.Alerts;
import logic.utils.LoggedUser;
import logic.view.ChatView;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCGroupChat extends EssentialGUI implements ChatView {

    @FXML
    private ListView<BMessage> chatMessagesLV;

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

    private ArrayList<BMessage> messages = new ArrayList<>();

    private ObservableList<BMessage> mexs;


    @FXML
    public void initialize() {
        cfacade.setChatGraphic(this);
    }

    @FXML
    void leaveGroup(MouseEvent event) {
        //query al database per uscire dal gruppo
        //anche notifica al server che sono uscito (rimuovere observer da hashmap)
        if(cfacade.leaveGroup(groupID)){
            alert.displayAlertPopup(Alerts.INFORMATION, "Group left successfully!");
            this.goToYourEvents(event);
        } else {
            alert.displayAlertPopup(Alerts.INFORMATION, "Group leaving failed :(");
        }
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

    @Override
    public void goToYourEvents(MouseEvent event){
        cfacade.setChatGraphic(null);
        super.goToYourEvents(event);
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

    private void setupChatLV(ListView<BMessage> chatMessagesLV) {
        chatMessagesLV.setFocusTraversable(false); // Disabilita la selezione degli elementi
        chatMessagesLV.setOrientation(Orientation.VERTICAL);
        chatMessagesLV.setCellFactory(param -> new ListCell<BMessage>() {
            @Override
            protected void updateItem(BMessage item, boolean empty) {
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

    private void populateChatLV(ArrayList<BMessage> messages) {
        mexs = FXCollections.observableArrayList(messages);
        /*for (BGroupMessage message : messages){
            chatMessagesLV.getItems().add(message);
        }*/
        chatMessagesLV.setItems(mexs);
        chatMessagesLV.scrollTo(chatMessagesLV.getItems().size() - 1);
    }

    // Metodo per allineare a destra o sinistra i messaggi
    private HBox createMessageNode(BMessage message) {
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

    @Override
    public synchronized void addMessageToChat(BMessage messageBean){//sempre generico, riusabile per private chat
        Platform.runLater(() -> {
            mexs.add(messageBean);
            chatMessagesLV.scrollTo(chatMessagesLV.getItems().size() - 1);
        });

    }



}
