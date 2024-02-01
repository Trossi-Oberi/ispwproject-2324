package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;


public class GCYourEventsOrg extends GCYourEventsGeneral{

    @FXML
    private ListView<String> analyticsLW;

    @FXML
    private ListView<String> musicLW;

    @FXML
    private ListView<String> pastEventsLW;

    @FXML
    private ListView<String> timeAndDateLW;

    @FXML
    private ListView<String> upcEventsLW;

    @FXML
    public void initialize() {
    }


}
