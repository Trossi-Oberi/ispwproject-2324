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
    public void update(MessageTypes type, ObjectOutputStream out){
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
