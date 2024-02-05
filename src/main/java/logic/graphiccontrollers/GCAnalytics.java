package logic.graphiccontrollers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.beans.BEvent;
import logic.controllers.CFacade;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;

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
    private static Integer timesClicked;    //volte che l'evento e' stato clickato (Random)
    private static Integer nParticipants; //partecipanti effettivi (Random tra 0 e participations)

    CFacade facadeController;

    @FXML
    public void initialize() {
        facadeController = new CFacade();
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
        int participations = facadeController.retrieveParticipationsToEvent(eventID);
        if (timesClicked == null){
            Random rand = new Random();
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
