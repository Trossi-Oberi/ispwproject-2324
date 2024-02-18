package logic.controllers;

import java.util.concurrent.Semaphore;

public class CServerInteraction {
    protected static Semaphore semaphore;
    protected ClientListener listener;
    protected static Thread listenerThread;
    protected CFacade facade;
}
