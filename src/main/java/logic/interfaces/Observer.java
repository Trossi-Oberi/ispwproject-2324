package logic.interfaces;

import logic.utils.NotificationTypes;

public interface Observer {
    void update(NotificationTypes type);
}
