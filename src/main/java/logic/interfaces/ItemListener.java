package logic.interfaces;

import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;

public interface ItemListener {
    void setupEventClickListener();
    void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean);
}
