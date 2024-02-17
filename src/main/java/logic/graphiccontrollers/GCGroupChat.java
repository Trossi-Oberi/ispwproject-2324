package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.beans.BGroupMessage;
import logic.utils.Alerts;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCGroupChat extends EssentialGUI {

    @FXML
    private ListView<String> chatMessagesLV;

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
        if(cfacade.sendMessageToGroup(this.groupID, messageTextField.getText())){
            messageTextField.clear();
        }else{
            alert.displayAlertPopup(Alerts.ERROR, "Error while sending message");
        }

    }

    public void initGroupChat(Integer groupID) {
        this.groupID = groupID;
        messages = cfacade.retrieveGroupChat(groupID);
        //populateChatLV(messages);
    }
}
