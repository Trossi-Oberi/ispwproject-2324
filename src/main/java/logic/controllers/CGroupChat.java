package logic.controllers;

import logic.beans.BGroupMessage;
import logic.dao.ChatDAO;
import logic.model.MGroupMessage;

import java.util.ArrayList;

public class CGroupChat {
    private ChatDAO chatDAO;

    public CGroupChat(){
        chatDAO = new ChatDAO();
    }
    public ArrayList<BGroupMessage> retrieveGroupChat(Integer groupID) {
        return makeBeansFromModels(chatDAO.retrieveGroupChat(groupID));
    }

    private ArrayList<BGroupMessage> makeBeansFromModels (ArrayList<MGroupMessage> models){
        ArrayList<BGroupMessage> beans = new ArrayList<>();
        for (MGroupMessage model : models){
            BGroupMessage bean = new BGroupMessage();
            bean.setSenderID(model.getSenderID());
            bean.setMessage(model.getMessage());
            beans.add(bean);
        }
        return beans;
    }

    public boolean writeMessage(Integer groupID, String text) {
        return chatDAO.writeMessage(groupID,text);
    }
}
