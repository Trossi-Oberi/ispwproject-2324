package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import logic.utils.Alerts;
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
        boolean res = alert.askDeleteConfirmation();
        if(res){
            if(cfacade.deleteEvent(eventBean.getEventID())){
                alert.displayAlertPopup(Alerts.INFORMATION, "Evento deleted succesfully");
                changeGUI(event, "YourEventsOrg.fxml");
            }else{
                alert.displayAlertPopup(Alerts.ERROR, "Error during event cancellation");
            }
        }
    }

    @FXML
    public void editEventAction(MouseEvent event) {
        preloadEditEvent(event);

    }

    private void preloadEditEvent(MouseEvent event) {
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
            logger.log(Level.SEVERE, "Error during preload\n", e);
        }
        nextGuiOnClick(event);
    }


}
