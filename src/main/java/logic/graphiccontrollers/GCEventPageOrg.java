package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GCEventPageOrg extends GCEventPageGeneral {

    @FXML
    private Button deleteEventBtn;

    @FXML
    private Button editEventBtn;

    @FXML
    public void deleteEventAction(MouseEvent event) {

    }

    @FXML
    public void editEventAction(MouseEvent event) {

    }

    @FXML
    public void goBack(MouseEvent event) {
        if(className.equals("GCYourEventsOrg")) {
            goToYourEvents(event);
        }
    }


}
