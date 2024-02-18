package logic.interfaces;

import logic.utils.NotificationTypes;

public interface NotiObserver {
    void update(NotificationTypes type);
}
