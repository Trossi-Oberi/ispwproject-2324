package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

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
        preloadEditEvent();
        changeGUI(event, "EditEvent.fxml");
    }

    @FXML
    public void goBack(MouseEvent event) {
        if (className.equals("GCYourEventsOrg")) {
            goToYourEvents(event);
        }
    }

    private void preloadEditEvent() {
        try {
            URL loc = EssentialGUI.class.getResource("EditEvent.fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            GCEditEvent editEventGC = loader.getController();
            editEventGC.initPageFromBean(eventBean);
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


}
