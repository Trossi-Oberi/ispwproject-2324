package logic.controllers;

import logic.beans.BEvent;
import logic.beans.BGroup;
import logic.dao.GroupDAO;
import logic.exceptions.GroupAlreadyCreated;
import logic.exceptions.InvalidGroupName;
import logic.model.MGroup;

import java.util.ArrayList;
import java.util.List;

public class CGroup {
    private GroupDAO groupDAO;


    public CGroup() {
        groupDAO = new GroupDAO();
    }

    //METODI CHE INTERAGISCONO COL SERVER
    /*joinGroup();    //associa al group id l'arraylist di user id
    leaveGroup();   //rimuove l'associazione
    deleteGroup();   //cancella la entry dalla hashmap*/

    public List<BGroup> retrieveGroups(List<BEvent> upcEventsList) {
        MGroup groupModel;
        List<MGroup> groupsModels = new ArrayList<>();
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

    private ArrayList<BGroup> makeBeanListFromModelList(List<MGroup> groupsModels) {
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

    public int createGroup(String groupName, int eventID) throws GroupAlreadyCreated, InvalidGroupName {
        if(groupName.isEmpty()){
            throw new InvalidGroupName("Group name cannot be null");
        }
        return groupDAO.createGroup(groupName, eventID);
    }

    public boolean joinGroup(Integer groupID) {
        return groupDAO.groupOperations(groupID, true);
    }

    public boolean leaveGroup(Integer groupID){
        return groupDAO.groupOperations(groupID, false);
    }

    public boolean checkUserInGroup(Integer groupID) {
        return groupDAO.checkUserInGroup(groupID);
    }
}
