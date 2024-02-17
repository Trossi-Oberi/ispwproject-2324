package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;

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
    
    @FXML
    void leaveGroup(MouseEvent event) {

    }

    @FXML
    void sendMessage(MouseEvent event) {

    }

    public void initGroupChat(Integer groupID) {
        cfacade.openGroupChat(groupID);
    }
}
