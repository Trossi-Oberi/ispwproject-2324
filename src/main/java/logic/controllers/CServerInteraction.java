package logic.controllers;

import java.util.concurrent.Semaphore;

public class CServerInteraction {

    private CServerInteraction(){};
    protected static Semaphore semaphore;
    protected static ClientListener listener;
    protected static Thread listenerThread;
    protected static CFacade facade;
}
