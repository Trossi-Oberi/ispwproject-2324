package logic.graphiccontrollers;

import logic.view.EssentialGUI;

public class GCYourEventsGeneral extends EssentialGUI {

    protected String formatTimeAndDate(String date, String time){
        return (date + " - "+time);
    }
}
