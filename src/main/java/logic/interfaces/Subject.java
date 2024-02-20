package logic.interfaces;

import logic.model.Notification;
import logic.utils.SubjectTypes;

import java.io.ObjectOutputStream;

public interface Subject {
    //Subject Interface For Server
    boolean attach(SubjectTypes type, Notification noti, ObjectOutputStream out);

    boolean detach(SubjectTypes type, Notification noti);

    boolean notify(SubjectTypes type, Object o);
}
