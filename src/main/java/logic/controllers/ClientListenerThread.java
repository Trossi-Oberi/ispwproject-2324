package logic.controllers;

import logic.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientListenerThread extends Thread{
    private Socket client;
    private ObjectInputStream in;
    private CNotification.NotiHandler handler;
    private boolean running;

    public ClientListenerThread(Socket client, CNotification.NotiHandler notiHandler) {
        this.client = client;
        this.handler = notiHandler;
        running = true;
        try {
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("thread client listener attivo");
            while (running) {
                // Ricevi un messaggio dal server
                Message receivedMsg = (Message) in.readObject();
                // Passa il messaggio all'handler per la gestione
                handler.handleMessage(receivedMsg);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stopRunning(){
        this.running = false;
        try {
            in.close();
        } catch (IOException e) {
            //ignore
        }
    }

}

