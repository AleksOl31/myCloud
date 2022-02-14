package ru.alexanna.cloud.client.model;

public interface Server {
    /**
     * Registers an observer object
     * @param o observer object
     */
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();

    /**
     *
     */
    void upload();
    void download();
    void changeCurrentDir(String selectedPath);
}
