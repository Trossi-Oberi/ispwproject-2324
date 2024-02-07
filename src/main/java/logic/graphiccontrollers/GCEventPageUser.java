package logic.graphiccontrollers;

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
import logic.utils.Alerts;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.net.URLEncoder;

public class GCEventPageUser extends GCEventPageGeneral {

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

    @FXML
    public void openMap(MouseEvent event) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.setTitle("Google Maps");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        try {
            String url = generateMapLink(eventBean.getEventAddress());
            webEngine.load(url);
        } catch (NullPointerException e){
            logger.log(Level.SEVERE, "No link generated");
        }

        VBox modalLayout = new VBox(10);
        modalLayout.getChildren().addAll(webView);
        modalLayout.setPadding(new Insets(10));

        modalStage.setScene(new Scene(modalLayout, 800, 600));
        modalStage.showAndWait();
    }

    private static String generateMapLink(String address){
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        return "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
    }
}
