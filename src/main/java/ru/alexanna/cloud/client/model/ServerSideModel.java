package ru.alexanna.cloud.client.model;

import ru.alexanna.cloud.client.model.connection.CloudConnection;
import ru.alexanna.cloud.model.FileMessage;
import ru.alexanna.cloud.model.PathChangeRequestMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServerSideModel implements Server {

    private final ArrayList<Observer> observers;

    private String serverDir;
    private List<String> serverFilesList;
                                // TODO change IP address here
    private final String HOST = "localhost"; /*"192.168.50.114";*/
    private final int PORT = 8189;
    private final CloudConnection connection;
    private final MessageHandler messageHandler = new MessageHandler(this);

    public ServerSideModel() {
        observers = new ArrayList<>();

        connection = new CloudConnection(HOST, PORT);
        connection.addMessageListener(messageHandler);
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(serverDir, serverFilesList);
        }
    }

    @Override
    public void upload(Path path) {
        try {
            FileMessage fileMessage = new FileMessage(path);
            connection.sendMessage(fileMessage);
//            connection.sendFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void download() {

    }

    @Override
    public void changeCurrentDir(String selectedPath) {
        connection.sendMessage(new PathChangeRequestMessage(selectedPath));
    }

    public String getServerDir() {
        return serverDir;
    }

    public void setServerDir(String serverDir) {
        this.serverDir = serverDir;
    }

    public List<String> getServerFilesList() {
        return serverFilesList;
    }

    public void setServerFilesList(List<String> serverFilesList) {
        this.serverFilesList = serverFilesList;
    }

}
