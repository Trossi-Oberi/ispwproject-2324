package logic.graphiccontrollers;

import logic.interfaces.InfoPages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import logic.view.EssentialGUI;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GCHelpFAQs extends EssentialGUI implements InfoPages  {
    @FXML
    private Hyperlink projectHyperlink;

    public void openLink(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(projectHyperlink.getText()));
        projectHyperlink.setVisited(false);
    }
}
