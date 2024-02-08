package logic.interfaces;

import logic.utils.MessageTypes;

import java.net.Socket;

public interface Observer {
    void update(MessageTypes type, Socket clientSocket);
}
