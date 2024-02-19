package logic.controllers;

import logic.beans.BEvent;
import logic.beans.BGroup;
import logic.dao.GroupDAO;
import logic.model.MGroup;

import java.util.ArrayList;

public class CGroup {
    private GroupDAO groupDAO;


    public CGroup() {
        groupDAO = new GroupDAO();
    }

    //METODI CHE INTERAGISCONO COL SERVER
    /*joinGroup();    //associa al group id l'arraylist di user id
    leaveGroup();   //rimuove l'associazione
    deleteGroup();   //cancella la entry dalla hashmap*/

    public ArrayList<BGroup> retrieveGroups(ArrayList<BEvent> upcEventsList) {
        MGroup groupModel;
        ArrayList<MGroup> groupsModels = new ArrayList<>();
        for (BEvent event : upcEventsList) {
            groupModel = groupDAO.retrieveGroupByEventID(event.getEventID());
            groupsModels.add(groupModel);
        }
        return makeBeanListFromModelList(groupsModels);

    }

    public BGroup getGroupByEventID(int eventID) {
        MGroup model = groupDAO.retrieveGroupByEventID(eventID);
        BGroup bean = new BGroup();
        if (model.getGroupID() != null) {
            bean.setGroupID(model.getGroupID());
            bean.setGroupName(model.getGroupName());
            bean.setEventID(model.getEventID());
            bean.setOwnerID(model.getOwnerID());
        }
        return bean;
    }

    private ArrayList<BGroup> makeBeanListFromModelList(ArrayList<MGroup> groupsModels) {
        ArrayList<BGroup> groupBeans = new ArrayList<>();
        for (MGroup model : groupsModels) {
            BGroup bean = new BGroup();
            if (model.getGroupID() != null) {
                bean.setGroupID(model.getGroupID());
                bean.setGroupName(model.getGroupName());
                bean.setEventID(model.getEventID());
                bean.setOwnerID(model.getOwnerID());
            }
            groupBeans.add(bean);
        }
        return groupBeans;
    }


    public boolean userInGroup(int userID, Integer groupID) {
        return groupDAO.userInGroup(userID, groupID);
    }

    public String getGroupNameByGroupID(Integer groupID) {
        return groupDAO.getGroupName(groupID);
    }

    public int createGroup(String groupName, int eventID) {
        return groupDAO.createGroup(groupName, eventID);
    }

    public boolean joinGroup(Integer groupID) {
        return groupDAO.joinGroup(groupID);
    }

    //TODO: fare controllo su gruppo già creato.

    public boolean leaveGroup(Integer groupID){
        return groupDAO.leaveGroup(groupID);
    }

    public boolean checkUserInGroup(Integer groupID) {
        return groupDAO.checkUserInGroup(groupID);
    }
}
