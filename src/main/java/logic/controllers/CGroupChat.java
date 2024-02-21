package logic.controllers;

import logic.beans.BMessage;
import logic.dao.ChatDAO;
import logic.model.MGroupMessage;
import logic.model.Message;
import logic.utils.LoggedUser;
import logic.utils.MessageTypes;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static logic.view.EssentialGUI.logger;

public class CGroupChat extends CServerInteraction{
    private ChatDAO chatDAO;
    private MessageFactory msgFactory;

    public CGroupChat(){
        this.chatDAO = new ChatDAO();
        this.msgFactory = new MessageFactory();
    }

    public List<BMessage> retrieveGroupChat(Integer groupID) {
        return makeBeansFromModels(chatDAO.retrieveGroupChat(groupID));
    }

    private List<BMessage> makeBeansFromModels (List<MGroupMessage> models){
        ArrayList<BMessage> beans = new ArrayList<>();
        for (MGroupMessage model : models){
            BMessage bean = new BMessage(model);
            beans.add(bean);
        }
        return beans;
    }

    public boolean writeMessage(BMessage message) {
        Message msgModel = msgFactory.createMessage(MessageTypes.GROUP, message.getSenderID(), message.getReceiverID(), message.getMessage());
        return chatDAO.writeMessage(msgModel);
    }

    public synchronized void sendMessage(MessageTypes msgType, BMessage message) {
        try {
            //creo il messaggio e lo mando al server
            if (listenerThread.isAlive()) {
                Message msg = msgFactory.createMessage(msgType, message.getSenderID(), message.getReceiverID(), message.getMessage());
                ObjectOutputStream out = LoggedUser.getOutputStream();
                out.writeObject(msg);
                out.flush();
                out.reset();
            }

            //aspetto sempre risposta del server
            semaphore.acquire(2);

        } catch (InvalidClassException e) {
            //gestione errore di serializzazione (writeObject)
            logger.severe("Cannot deserialize object");
        } catch (InterruptedException e) {
            //gestione eccezioni IO o interruzione thread
            Thread.currentThread().interrupt();
        } catch (IOException e){
            logger.severe(e.getMessage());
        }
    }
}
