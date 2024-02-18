package logic.interfaces;

import logic.model.Message;

public interface MessageObserver {
    void update(Message message);
}
