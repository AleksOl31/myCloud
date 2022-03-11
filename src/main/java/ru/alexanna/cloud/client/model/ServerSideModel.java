package ru.alexanna.cloud.client.model;

import ru.alexanna.cloud.client.model.connection.CloudConnection;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.DataOutputStream;
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

    public ServerSideModel() {
        observers = new ArrayList<>();
        connection = new CloudConnection(HOST, PORT);
        connection.addMessageListener(new ByteMessageHandler(this));
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

    }

    @Override
    public void download() {

    }

    @Override
    public void changeCurrentDir(String selectedPath) {
//        connection.sendMessage(new PathChangeRequestMessage(selectedPath));
    }

    @Override
    public void doAuthenticate(String username, String password) {
        DataOutputStream os = connection.getOs();
        try {
            os.writeByte(FileCommand.DO_AUTH);
            os.writeUTF(username + " " + password);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public CloudConnection getConnection() {
        return connection;
    }
}
