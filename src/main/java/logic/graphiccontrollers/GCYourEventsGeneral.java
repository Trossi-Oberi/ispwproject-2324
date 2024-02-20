package logic.graphiccontrollers;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.interfaces.DoubleClickListener;
import logic.view.EssentialGUI;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class GCYourEventsGeneral extends EssentialGUI implements DoubleClickListener {

    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    protected String formatTimeAndDate(String date, String time){
        return (date + " - "+ time);
    }

    @Override
    public abstract void setupEventClickListener();
    @Override
    public abstract void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean, String fxmlpage);

    public static BEvent getBeanFromListView(ListView<String> lv, List<BEvent> beansArray){
        int selectedEventIndex = lv.getSelectionModel().getSelectedIndex();
        if(selectedEventIndex == -1){
            return null;
        }
        return beansArray.get(selectedEventIndex);
    }
}
