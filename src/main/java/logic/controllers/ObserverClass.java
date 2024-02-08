package logic.controllers;

import logic.interfaces.Observer;
import logic.model.Message;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObserverClass implements Observer {
    private String userCity;
    private int id; //generico, sia per User che per Organizer

    public ObserverClass(int id){
        this.id = id;
    }

    @Override
    public void update(MessageTypes type, Socket clientSocket){
        try {
            ObjectOutputStream objOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            Message message = new Message(type);
            objOutputStream.writeObject(message);
            objOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
