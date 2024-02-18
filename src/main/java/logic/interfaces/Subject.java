package logic.interfaces;

public interface Subject {
    //dato che usiamo una hashmap passiamo anche la citta' nei metodi attach e detach per identificare la giusta associazione citta'-lista observers
    void attachObserver(String city, NotiObserver observer);
    void detachObserver(String city, NotiObserver observer);
    void notifyObservers(String city);

    //TODO: Fare 3 subject separati da implementare nel server: User-City, Org-Event, User-Group
}
