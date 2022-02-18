package ru.alexanna.cloud.client.model;

import java.nio.file.Path;

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
     * @param path
     */
    void upload(Path path);
    void download();
    void changeCurrentDir(String selectedPath);
}
