package ru.alexanna.cloud.client.model;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.client.model.connection.CloudConnection;
import ru.alexanna.cloud.io.general.Command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerSideModel implements CloudServer, Command {

    private final ArrayList<Observer> observers;

    private String serverDir;
    private List<String> serverFilesList;
                                // TODO change IP address here
    private final String HOST = "localhost"; /*"192.168.50.114";*/
    private final int PORT = 8189;
    private final CloudConnection connection;

    public ServerSideModel() {
        observers = new ArrayList<>();
        connection = new CloudConnection(HOST, PORT);
        connection.addMessageListener(new ByteMessageHandler(this));
    }

    /**
     * Observable interface method implements
     * @param o observer object
     */
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
            observer.updateServerSideState(serverDir, serverFilesList);
        }
    }

    /**
     * CloudServer interface method implements
     * @param path
     */
    @Override
    public void upload(Path path) {

    }

    @Override
    public void download(String path) {

    }

    @Override
    public void changeCurrentDir(String selectedPath) {
//        connection.sendMessage(new PathChangeRequestMessage(selectedPath));
    }

    @Override
    public void doAuthenticate(String username, String password) {
        connection.sendMessage(DO_AUTH, username + " " + password);
    }

    @Override
    public void sendCommand(byte command) {
        connection.sendCommand(command);
    }

    @Override
    public String getServerDir() {
        return serverDir;
    }

    @Override
    public void setServerDir(String serverDir) {
        this.serverDir = serverDir;
    }

    @Override
    public List<String> getServerFilesList() {
        return serverFilesList;
    }

    @Override
    public void setServerFilesList(List<String> serverFilesList) {
        this.serverFilesList = serverFilesList;
        notifyObservers();
    }

    @Override
    public CloudConnection getConnection() {
        return connection;
    }
}
