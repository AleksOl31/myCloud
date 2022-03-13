package ru.alexanna.cloud.client.model;

public interface Observable {
    /**
     * Registers an observer object
     * @param o observer object
     */
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
