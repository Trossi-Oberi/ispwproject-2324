package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import logic.exceptions.DuplicateEventParticipation;
import logic.utils.Alerts;

public class GCEventPageUser extends GCEventPageGeneral {

    @FXML
    private Button participateEventBtn;

    @FXML
    void goBack(MouseEvent event) {
        goToYourEvents(event);
    }

    @FXML
    public void participateToEvent(MouseEvent event) {
        try {
            if(cfacade.participateToEvent(this.eventBean)){
                alert.displayAlertPopup(Alerts.INFORMATION, "Event participation successfully added!\nYou can now view it on Your Events page");
                goBack(event);
            } else {
                alert.displayAlertPopup(Alerts.ERROR, "Event participation failed :(");
                goBack(event);
            }
        } catch (DuplicateEventParticipation e) {
            alert.displayAlertPopup(Alerts.WARNING, "Event participation already planned");
            goBack(event);
        }
    }
    @FXML
    void openMap(MouseEvent event) {

    }




}
