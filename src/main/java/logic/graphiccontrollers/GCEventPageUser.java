package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import logic.exceptions.DuplicateEventParticipation;
import logic.interfaces.InfoPages;
import logic.utils.Alerts;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.net.URLEncoder;

public class GCEventPageUser extends GCEventPageGeneral implements InfoPages {

    @FXML
    private Button participateEventBtn;

    @FXML
    private Button viewMap;

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

    @Override
    public void openLink(ActionEvent event) throws URISyntaxException, IOException {
        String link = generateMapLink(eventBean.getEventAddress());
        Desktop.getDesktop().browse(new URI(link));
    }

    private static String generateMapLink(String address){
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        return "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
    }
}
