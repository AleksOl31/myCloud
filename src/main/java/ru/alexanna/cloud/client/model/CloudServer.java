package ru.alexanna.cloud.client.model;

import ru.alexanna.cloud.client.model.connection.CloudConnection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface CloudServer extends Observable {
    /**
     *
     * @param path
     */
    void upload(Path path);
    void download(String fileName);
    void changeCurrentDir(String selectedPath);
    void doAuthenticate(String username, String password);
    void sendCommand(byte command);
    CloudConnection getConnection();
    String getServerDir();
    void setServerDir(String serverDir);
    List<String> getServerFilesList();
    void setServerFilesList(List<String> serverFilesList);
    void writeFile(String fileName, long fileSize) throws IOException;
}
