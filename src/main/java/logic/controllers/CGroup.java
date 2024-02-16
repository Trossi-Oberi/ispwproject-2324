package logic.controllers;

import logic.beans.BEvent;
import logic.beans.BGroup;
import logic.dao.EventDAO;
import logic.dao.GroupDAO;
import logic.dao.UserEventDAO;
import logic.model.MGroup;

import java.util.ArrayList;

public class CGroup {
    private GroupDAO groupDAO;


    public CGroup(){
        groupDAO = new GroupDAO();
    }

    public ArrayList<BGroup> retrieveGroups(ArrayList <BEvent> upcEventsList){
        MGroup groupModel;
        ArrayList <MGroup> groupsModels = new ArrayList<>();
        for (BEvent event : upcEventsList){
            groupModel = groupDAO.retrieveGroupByEventID(event.getEventID());
            groupsModels.add(groupModel);
        }
        return makeBeanListFromModelList(groupsModels);

    }

    private ArrayList<BGroup> makeBeanListFromModelList(ArrayList<MGroup> groupsModels) {
        ArrayList <BGroup> groupBeans = new ArrayList<>();
        for (MGroup model : groupsModels){
            BGroup bean = new BGroup();
            if (model.getGroupID()!=null){
                bean.setGroupID(model.getGroupID());
                bean.setGroupName(model.getGroupName());
                bean.setEventID(model.getEventID());
                bean.setOwnerID(model.getOwnerID());
            }
            groupBeans.add(bean);
        }
        return groupBeans;
    }
}
