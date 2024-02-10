package logic.controllers;

import logic.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ClientListener implements Runnable{
    //il client listener si occupa di gestire la comunicazione in input tra server e client

    private Socket client;
    private Semaphore semaphore;
    private int clientID;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean listenerRunning = true;

    public ClientListener(int id, Semaphore semaphore, CNotification notiController){
        clientID = id;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            client = new Socket(Server.ADDRESS, Server.PORT);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        if(listenerRunning){
            semaphore.release(2);

        }

    }
}
