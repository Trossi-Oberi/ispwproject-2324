package logic.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.Alert;

import logic.utils.Alerts;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertPopup {
    private String imagePath;
    private String absolutePath;
    private Image image;
    private Logger logger;
    private Alert.AlertType type;

    private String headerText = null;

    public AlertPopup() {
        this.logger = Logger.getLogger("NightPlan");
    }

    public void displayAlertPopup(Alerts alertType, String text) {

        /*
        Stage error = new Stage();
        error.setWidth(400);
        error.setHeight(150);
        error.initModality(Modality.APPLICATION_MODAL);

        ImageView imageView = new ImageView(image);
        switch(alertType) {
            case ERROR:
                try {
                    image = new Image(new FileInputStream("icons/alert.png"), 30.0, 30.0, true, true);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                imageView.setImage(image);
                break;
            case NOTIFY:
                try {
                    image = new Image(new FileInputStream("icons/edit.png"), 30.0, 30.0, true, true);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case SUGGEST:
                try {
                    image = new Image(new FileInputStream("icons/cuore.png"), 30.0, 30.0, true, true);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                return;
        }

        Text label = new Text();
        label.setText(text);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> error.close());
        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        error.setScene(scene);
        error.setTitle("NightPlan");
        error.setResizable(false);
        error.showAndWait();
        */
        try {
            switch (alertType) {
                case ERROR:
                    imagePath = "/icons/fatal_error.png";
                    type = Alert.AlertType.ERROR;
                    headerText = "A severe error has occurred";
                    break;
                case WARNING:
                    imagePath = "/icons/error";
                    type = Alert.AlertType.WARNING;
                    headerText = "Warning";
                    break;
                case CONFIRMATION:
                    imagePath = "/icons/help.png";
                    type = Alert.AlertType.CONFIRMATION;
                    break;
                case INFORMATION:
                    imagePath = "/icons/danger.png";
                    type = Alert.AlertType.INFORMATION;
                    headerText = "Alert";
                    break;
            }
            //absolutePath = getClass().getResource(imagePath).toExternalForm();
            //image = new Image(absolutePath);
        } catch (IllegalArgumentException e){
            logger.log(Level.SEVERE, "Image path not found!", e);
            return;
        }
        Alert alert = new Alert(type);
        //alert.setGraphic(new ImageView(image));
        alert.setTitle("NightPlan");
        alert.setContentText(text);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}