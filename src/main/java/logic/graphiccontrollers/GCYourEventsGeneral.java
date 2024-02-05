package logic.graphiccontrollers;

import logic.view.EssentialGUI;

import java.time.format.DateTimeFormatter;

public class GCYourEventsGeneral extends EssentialGUI {

    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    protected String formatTimeAndDate(String date, String time){
        return (date + " - "+ time);
    }
}
