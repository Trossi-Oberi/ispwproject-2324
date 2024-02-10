package logic.interfaces;

import logic.utils.MessageTypes;

import java.io.ObjectOutputStream;
import java.net.Socket;

public interface Observer {
    void update(MessageTypes type);
}
