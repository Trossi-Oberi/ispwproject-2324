package logic.graphiccontrollers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.util.Random;

import logic.beans.BEvent;
import logic.view.EssentialGUI;

public class GCAnalytics extends EssentialGUI {
    @FXML
    private Label addressL;

    @FXML
    private Label cityL;

    @FXML
    private Label dateL;

    @FXML
    private Label eventNameL;

    @FXML
    private Button goBackBtn;

    @FXML
    private Label participantsL;

    @FXML
    private Label plannedL;

    @FXML
    private Label timesClickedL;

    @FXML
    private ImageView eventImage;

    private int eventID;
    private static Integer timesClicked;
    private static Integer nParticipants; //partecipanti effettivi (Random tra 0 e participations)


    @FXML
    public void initialize() {
        //empty
    }

    public void initAnalyticsByBean(BEvent eventBean){
        this.eventID = eventBean.getEventID();
        eventNameL.setText(eventBean.getEventName());
        cityL.setText(eventBean.getEventCity());
        addressL.setText(eventBean.getEventAddress());
        dateL.setText(eventBean.getEventDate());
        try {
            Image eventImage = new Image(new ByteArrayInputStream(eventBean.getEventPicData()));
            this.eventImage.setImage(eventImage);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

    }

    public void initParticipantsInfo(){
        //retrieve del numero delle participations
        //partecipazioni segnate (Query dal DB UserEvent)
        int participations = cfacade.retrieveParticipationsToEvent(eventID);
        if (timesClicked == null){
            Random rand = new SecureRandom();
            timesClicked = rand.nextInt(5000- participations +1) + participations;
            nParticipants = rand.nextInt(participations +1);
        }
        timesClickedL.setText(""+timesClicked);
        plannedL.setText(""+ participations);
        participantsL.setText(""+nParticipants);
    }

    @FXML
    void exportAnalyticsFile(MouseEvent event) {

    }
}
