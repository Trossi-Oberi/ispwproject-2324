package logic.model;

import logic.utils.MessageTypes;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageTypes messageType;
    private String city;
    private int clientID;
    private int eventID;

    //implementare message factory

    public Message(MessageTypes type){
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

    public MessageTypes getType(){
        return messageType;
    }

    public String getCity(){
        return this.city;
    }

    public int getClientID(){
        return this.clientID;
    }

    public int getEventID(){return this.eventID;}
}
