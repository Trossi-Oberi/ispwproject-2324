package logic.controllers;

import logic.beans.*;
import logic.exceptions.InvalidTokenValue;
import logic.model.Message;
import logic.server.Server;
import logic.utils.*;
import logic.view.ChatView;
import logic.view.NotificationView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static logic.view.EssentialGUI.logger;

public class CFacade {
    private CLogin loginController;
    private CRegistration regController;
    private CManageEvent manageEventController;
    private CNotification notificationController;
    private CAnalytics analyticsController;
    private CGroup groupController;
    private CGroupChat chatController;
    private ChatView chatView;
    private static NotificationView notiView;

    public CFacade() {
        //empty
    }

    //Metodi che interagiscono col server inviando notifiche
    public boolean addEvent(BEvent bean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.addEvent(bean); //chiamata al controller effettivo
        if (res) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.EventAdded, bean.getEventOrganizerID(), null, bean.getEventID(), null, bean.getEventCity(), null, null);
        }
        return res;
    }

    public boolean deleteEvent(int eventID) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.deleteEvent(eventID);
        if (res) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.EventDeleted, null, null, eventID, null, null, null, null);
        }
        return res;
    }

    public boolean participateToEvent(BEvent eventBean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.participateToEvent(eventBean);
        if (res) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.UserEventParticipation, LoggedUser.getUserID(), null, eventBean.getEventID(), null, null, null, null);
        }
        return res;
    }

    public boolean removeEventParticipation(BEvent eventBean) {
        boolean result = false;

        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        boolean res = manageEventController.removeEventParticipation(eventBean);
        if (res) {
            notificationController.sendNotification(NotificationTypes.UserEventRemoval, LoggedUser.getUserID(), null, eventBean.getEventID(), null, null, null, null);
            Integer groupID = getGroupByEventID(eventBean.getEventID()).getGroupID();
            //se il gruppo non esiste salto il leaveGroup
            if (groupID == null || !checkUserInGroup(groupID)) {
                //gruppo non esistente o utente non nel gruppo
                return true;
            }

            //eseguo questo se gruppo esiste e l'utente ne fa parte
            if (groupController == null) {
                groupController = new CGroup();
            }
            result = groupController.leaveGroup(groupID);
            if (result) {
                if (notificationController == null) {
                    notificationController = new CNotification(this);
                }
                notificationController.sendNotification(NotificationTypes.GroupLeave, LoggedUser.getUserID(), null, groupID, null, null, null, null);
            }
        }
        return result;
    }

    private boolean checkUserInGroup(Integer groupID) {
        if (groupController == null) {
            groupController = new CGroup();
        }

        return groupController.checkUserInGroup(groupID);
    }

    public boolean checkPreviousEventParticipation(BEvent eventBean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.checkPreviousEventParticipation(eventBean);
    }


    public boolean registerUser(BUserData bean) throws RuntimeException {
        if (regController == null) {
            regController = new CRegistration();
        }
        boolean res = regController.registerUserControl(bean);

        //se completo con successo la registrazione di un nuovo utente allora effettuo una connessione al server
        if (res) {
            if (bean.getUserType().equals(UserTypes.USER)) {
                if (notificationController == null) {
                    notificationController = new CNotification(this); //inizializzo il controller delle notifiche
                }
                //setup temp socket per la registrazione (viene automaticamente chiusa dopo l'invio della notifica UserRegistration response dal server
                setupTempSocket();
                notificationController.sendNotification(NotificationTypes.UserRegistration, bean.getUserID(), null, null, null, bean.getCity(), null, null); //null ovvio che sia userType
            }
        }
        return res;
    }

    private void setupTempSocket() {
        ObjectOutputStream out = null;
        SecureObjectInputStream in = null;
        Socket client = null;
        try {
            client = new Socket(Server.ADDRESS, Server.PORT);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new SecureObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        LoggedUser.setSocket(client);
        LoggedUser.setOutputStream(out);
        LoggedUser.setInputStream(in);
    }

    public int loginUser(BUserData bean, boolean isGoogleAuth, String authCode, NotificationView notiView) throws InvalidTokenValue, RuntimeException {
        if (loginController == null) {
            loginController = new CLogin();
        }
        int loginRes = loginController.checkLoginControl(bean, isGoogleAuth, authCode);
        if (loginRes == 1) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.LoggedIn, LoggedUser.getUserID(), null, null, null, LoggedUser.getCity(), null, LoggedUser.getUserType());
        }
        return loginRes;
    }

    public void signOut() {
        //effettuo la disconnessione dal server
        if (notificationController == null) {
            notificationController = new CNotification(this);
        }
        notificationController.sendNotification(NotificationTypes.Disconnected, LoggedUser.getUserID(), null, null, null, null, null, LoggedUser.getUserType());
        //dopo la disconnessione dal server chiudo la sessione di Login
        if (loginController == null) {
            loginController = new CLogin();
        }
        loginController.closeLoginSession();
    }

    public boolean createGroup(String groupName, int eventID) {
        boolean res = false;

        if (groupController == null) {
            groupController = new CGroup();
        }
        int newGroupID = groupController.createGroup(groupName, eventID); //res == new groupID
        if (newGroupID > 0) {
            res = this.joinGroup(newGroupID);
        }
        return res;
    }

    public boolean joinGroup(Integer groupID) {
        if (groupController == null) {
            groupController = new CGroup();
        }
        boolean res = groupController.joinGroup(groupID);
        if (res) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            //anche qui groupID passato al posto di eventID
            notificationController.sendNotification(NotificationTypes.GroupJoin, LoggedUser.getUserID(), null, groupID, null, null, null, null);
        }
        return res;
    }

    public boolean leaveGroup(Integer groupID) {
        if (groupController == null) {
            groupController = new CGroup();
        }

        boolean result = groupController.leaveGroup(groupID);
        if (result) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.GroupLeave, LoggedUser.getUserID(), null, groupID, null, null, null, null);
        }
        return result;
    }

    public boolean sendMessageToGroup(Integer groupID, String text) {
        if (chatController == null) {
            chatController = new CGroupChat();
        }
        boolean res = chatController.writeMessage(groupID, text);
        if (res) {
            chatController.sendMessage(MessageTypes.GROUP, LoggedUser.getUserID(), groupID, text);
        }
        return res;
    }


    //Metodi che non interagiscono col server
    public List<BEvent> retrieveEvents(UserTypes userType, String className) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.retrieveMyEvents(userType, className);
    }

    public boolean editEvent(BEvent eventBean) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.editEvent(eventBean);
    }


    public int retrieveParticipationsToEvent(int id) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.getParticipationsToEvent(id);
    }


    public ArrayList<String> getProvincesList() {
        if (regController == null) {
            regController = new CRegistration();
        }
        return regController.getProvincesList();
    }

    public ArrayList<String> getCitiesList(String province) {
        if (regController == null) {
            regController = new CRegistration();
        }
        return regController.getCitiesList(province);
    }


    public ArrayList<BNotification> retrieveNotifications(int userID) {
        if (notificationController == null) {
            notificationController = new CNotification(this);
        }
        return notificationController.retrieveNotifications(userID);
    }

    public String getEventNameByEventID(int eventID) {
        if (manageEventController == null) {
            manageEventController = new CManageEvent();
        }
        return manageEventController.getEventNameByEventID(eventID);
    }

    public int changeUserCity(int userID, String newProvince, String newCity) {
        if (loginController == null) {
            loginController = new CLogin();
        }

        String oldCity = LoggedUser.getCity();
        int res = loginController.changeCity(userID, newProvince, newCity);

        if (res == 1) {
            if (notificationController == null) {
                notificationController = new CNotification(this);
            }
            notificationController.sendNotification(NotificationTypes.ChangeCity, LoggedUser.getUserID(), null, null, null, oldCity, newCity, null);
        }
        return res;
    }

    public String getUsernameByID(int userID) {
        if (loginController == null) {
            loginController = new CLogin();
        }
        return loginController.getUsernameByID(userID);
    }

    public boolean deleteNotification(Integer notificationID, ArrayList<BNotification> notificationsList, int index) {
        if (notificationController == null) {
            notificationController = new CNotification(this);
        }

        return notificationController.deleteNotification(notificationID, notificationsList, index);
    }

    public ArrayList<BGroup> retrieveGroups(ArrayList<BEvent> upcomingEventsList) {
        if (groupController == null) {
            groupController = new CGroup();
        }
        return groupController.retrieveGroups(upcomingEventsList);
    }

    public BGroup getGroupByEventID(int eventID) {
        if (groupController == null) {
            groupController = new CGroup();
        }
        return groupController.getGroupByEventID(eventID);
    }

    public boolean userInGroup(int userID, Integer groupID) {
        if (groupController == null) {
            groupController = new CGroup();
        }
        return groupController.userInGroup(userID, groupID);
    }

    public String getGroupNameByGroupID(Integer groupID) {
        if (groupController == null) {
            groupController = new CGroup();
        }
        return groupController.getGroupNameByGroupID(groupID);
    }

    public ArrayList<BMessage> retrieveGroupChat(Integer groupID) {
        if (chatController == null) {
            chatController = new CGroupChat();
        }
        return chatController.retrieveGroupChat(groupID);
    }

    public void setNotiGraphic(NotificationView notificationView) {
        notiView = notificationView;
    }

    public void setChatGraphic(ChatView chatView) {
        this.chatView = chatView;
    }

    public void showNotification(NotificationTypes notiType) {
        notiView.showNotification(notiType);
    }

    public void addMessageToChat(Message msg) {
        //anche qui l'unico caso e' quello della group chat quindi non faccio controlli aggiuntivi
        BMessage beanMsg = new BMessage(msg);
        this.chatView.addMessageToChat(beanMsg);
    }


    public ChatView getChatGraphic() {
        return this.chatView;
    }

    public boolean exportAnalyticsFile(BAnalytics analysis) {
        if (analyticsController == null) {
            analyticsController = new CAnalytics();
        }
        return analyticsController.exportAnalyticsFile(analysis);
    }
}
