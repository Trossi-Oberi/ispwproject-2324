package logic.model;

import logic.utils.MessageTypes;
import logic.utils.UserTypes;

import java.io.Serializable;

public interface Message extends Serializable {

    void setMessageType(MessageTypes msgType);

    void setCity(String city);

    void setClientID(Integer clientID);

    void setEventID(Integer eventID);

    void setUserType(UserTypes userType);


    MessageTypes getMessageType();

    String getCity();

    Integer getClientID();

    Integer getEventID();

    UserTypes getUserType();
}


    /*public Message(MessageTypes type){
        messageType = type;
    }

    public Message(MessageTypes type, int clientID){
        this(type);
        this.clientID = clientID;
        //caso3: utente si logga
        //type: UserLogin, viene instaurata la connessione tramite socket e il listener rimane in attesa di notifiche


        //caso 4: organizer si logga
        //type: OrganizerLogin, stessa cosa di UserLogin

    }

    //caso 1: USER REGISTRATION
    //type: UserRegistration, city: citta' relativa all'user, id = user_id
    //Funzionamento notifica: nuovo evento viene inserito, prendo citta' dell'evento e aggiorno tutti gli observer con la stessa citta', inviando loro una notifica
    //Gli UserObserver devono incorporare l'userID altrimenti non so a chi inviare la notifica e a chi no
    public Message(MessageTypes type, int clientID, String city){
        this(type, clientID);
        this.city = city;
    }

    public Message(MessageTypes type, int clientID, String city, UserTypes usrType) {
        this(type, clientID, city);
        this.userType = usrType;
    }

    //caso 2: organizer aggiunge evento
    //type: EventAdded, eventID: id dell'evento che e' stato aggiunto. Verra' creato dal server un OrganizerObserver che viene aggiornato quando un utente clickera' su partecipa
    //Funzionamento notifica: utente clicka su partecipa evento, prendo event id, ricavo organizer_id, aggiorno l'observer relativo all'organizer inviandogli una notifica
    public Message(MessageTypes type, int clientID, int eventID, String city){
        this(type, clientID, city);
        this.eventID = eventID;
    }

    //caso 3: utente clicka partecipa all'evento

    //id e city vanno bene sia per caso 1 che per caso 2:
    // in caso 1 id sara' id utente e city sara' usercity,
    // in caso 2 id sara' id evento e city sara' eventcity
     */


