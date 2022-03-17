package ru.alexanna.cloud.client.model;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.client.model.connection.CloudConnection;
import ru.alexanna.cloud.io.general.Command;
import ru.alexanna.cloud.io.general.FileCommand;
import ru.alexanna.cloud.io.general.FileCommandExecutor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerSideModel implements CloudServer, Command {

    private final ArrayList<Observer> observers;

    private String serverDir;
    private List<String> serverFilesList;
    private String percentCompleted;
                                // TODO change IP address here
    private final String HOST = /*"10.70.29.158";*/ "localhost"; /*"192.168.50.114";*/
    private final int PORT = 8189;
    private final CloudConnection connection;
    private FileCommand clientSideModel;

    public ServerSideModel(FileCommand clientSideModel) {
        this.clientSideModel = clientSideModel;
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
    public void notifyServerStateObservers() {
        for (Observer observer : observers) {
            observer.updateServerSideState(serverDir, serverFilesList);
        }
    }

    @Override
    public void notifyClientStateObservers() {
        for (Observer observer : observers) {
            observer.updateClientSideState();
        }
    }

    /**
     * CloudServer interface method implements
     * @param path path to the transferred file
     */
    @Override
    public void upload(Path path) {
        connection.sendFile(path);
    }

    @Override
    public void download(String fileName) {
        connection.sendMessage(GET_FILE, fileName);
    }

    @Override
    public void writeFile(String fileName, long fileSize) throws IOException {
        clientSideModel.writeFile(fileName, fileSize, connection.getIs(), (bytesCompleted) -> {});
        notifyClientStateObservers();
    }

    @Override
    public void changeCurrentDir(String selectedPath) {
        connection.sendMessage(CHANGE_PATH_REQUEST, selectedPath);
        notifyServerStateObservers();
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
        notifyServerStateObservers();
    }

    @Override
    public CloudConnection getConnection() {
        return connection;
    }

}
