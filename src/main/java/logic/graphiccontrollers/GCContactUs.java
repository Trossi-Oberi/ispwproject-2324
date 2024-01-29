package logic.graphiccontrollers;

import javafx.fxml.FXML;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Hyperlink;
import javafx.event.ActionEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GCContactUs extends EssentialGUI{
    @FXML
    private Hyperlink mattHyperlink;

    @FXML
    private Hyperlink nicoHyperlink;

    @FXML
    void openLink(ActionEvent event) throws URISyntaxException, IOException {
        if (nicoHyperlink.isVisited()){
            Desktop.getDesktop().browse(new URI(nicoHyperlink.getText()));
            nicoHyperlink.setVisited(false);
        }else{
            Desktop.getDesktop().browse(new URI(mattHyperlink.getText()));
            mattHyperlink.setVisited(false);
        }

    }


}


