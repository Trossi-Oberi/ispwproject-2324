package logic.interfaces;

public interface Subject {
    //dato che usiamo una hashmap passiamo anche la citta' nei metodi attach e detach per identificare la giusta associazione citta'-lista observers
    void attachObserver(String city, Observer observer);
    void detachObserver(String city, Observer observer);
    void notifyObservers(String city);
}
