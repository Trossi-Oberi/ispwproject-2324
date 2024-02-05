package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import logic.beans.BEvent;
import logic.exceptions.DuplicateEventParticipation;
import logic.utils.Alerts;

import java.io.*;

public class GCEventPage extends GCHomeUser {

    @FXML
    private Text address;

    @FXML
    private Text city;

    @FXML
    private Text dateTime;

    @FXML
    private ImageView eventImage;

    @FXML
    private Text eventName;

    @FXML
    private Button goBackBtn;

    @FXML
    private Text musicGenre;

    @FXML
    private Text organizerName;

    @FXML
    private Button participateEventBtn;

    private BEvent eventBean;

    @FXML
    public void initialize(){

    }

    public void initEventBean(BEvent eventB){
        this.eventName.setText(eventB.getEventName());
        this.city.setText(eventB.getEventCity());
        this.address.setText(eventB.getEventAddress());
        this.dateTime.setText(eventB.getEventDate() + " " + eventB.getEventTime());
        this.organizerName.setText(eventB.getEventOrganizer());
        this.musicGenre.setText(eventB.getEventMusicGenre());

        try {
            Image eventImage = new Image(new ByteArrayInputStream(eventB.getEventPicData()));
            this.eventImage.setImage(eventImage);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

        //salvo localmente il bean evento
        this.eventBean = eventB;
    }

    @FXML
    public void goBack(MouseEvent event) {
        goToHome(event);
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
}
