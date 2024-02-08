/*package logic.controllers;

import logic.interfaces.Notifier;
import logic.interfaces.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserNotifier implements Notifier {
    //notifica gli utenti dell'aggiunta di un nuovo evento nella loro citta'
    //Singleton Pattern
    private static UserNotifier instance = null;
    private Map<String, List<Observer>> observersByCity;

    private UserNotifier() {
        this.observersByCity = new HashMap<>();
    }

    public static UserNotifier getInstance(){
        if (UserNotifier.instance == null){
            UserNotifier.instance = new UserNotifier();
        }
        return UserNotifier.instance;
    }

    @Override
    public void attachObserver(String city, Observer observer) {
        observersByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(observer);
    }
    @Override
    public void detachObserver(String city, Observer observer) {
        observersByCity.getOrDefault(city, new ArrayList<>()).remove(observer);
    }
    @Override
    public void notifyObservers(String eventCity) {
        List<Observer> observers = observersByCity.getOrDefault(eventCity, new ArrayList<>());
        for (Observer observer : observers) {
            observer.update(eventCity);
        }
    }
}*/
