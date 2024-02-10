package logic.controllers;

import logic.interfaces.Observer;
import logic.model.Message;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObserverClass implements Observer {
    private int id; //generico, sia per User che per Organizer
    private ObjectOutputStream out;

    public ObserverClass(int id, ObjectOutputStream out){
        this.id = id;
        this.out = out;
    }

    @Override
    public void update(MessageTypes type){
        try {
            Message message = new Message(type);
            out.writeObject(message);
        } catch (IOException e) {
            System.out.println("Errore nell'update function di observerclass "+e.getMessage());
        }
    }
    public int getObsID(){
        return this.id;
    }
}
