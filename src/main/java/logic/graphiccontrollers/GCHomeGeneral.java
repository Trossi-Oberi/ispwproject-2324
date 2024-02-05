package logic.graphiccontrollers;

import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.controllers.CFacade;
import logic.interfaces.ItemListener;
import logic.view.AlertPopup;
import logic.view.EssentialGUI;

import java.util.ArrayList;

public class GCHomeGeneral extends EssentialGUI implements ItemListener {

    protected ArrayList<BEvent> eventsList = new ArrayList<>();

    public void initialize(){

    }

    @Override
    public void setupEventClickListener() {

    }

    @Override
    public void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean) {

    }
}
