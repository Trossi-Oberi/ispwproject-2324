package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.beans.BGroupMessage;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCGroupChat extends EssentialGUI {

    @FXML
    private ListView<?> chatMessagesLV;

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

    private ArrayList<BGroupMessage> messages = new ArrayList<>();
    
    @FXML
    void leaveGroup(MouseEvent event) {
        //query al database per uscire dal gruppo
    }

    @FXML
    void sendMessage(MouseEvent event) {
        //scrive messaggio sul database e lo gira al server
    }

    public void initGroupChat(Integer groupID) {
        messages = cfacade.retrieveGroupChat(groupID);
    }
}
