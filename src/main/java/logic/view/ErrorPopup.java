package logic.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorPopup {
    public void displayErrorPopup(String text) {
        Stage window = new Stage();
        window.setWidth(400);
        window.setHeight(150);
        window.initModality(Modality.APPLICATION_MODAL);
        Text label = new Text();
        label.setText(text);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.setTitle("NightPlan");
        window.setResizable(false);
        window.showAndWait();

    }
}