package ru.alexanna.cloud.client.model;

import java.util.List;

public interface Observer {
    /**
     * Updates the server's current folder and file list after a notification is received
     * from an object that implements the Server interface
     * @param serverDir current server folder
     *
     * @param serverFilesList list of files in the current folder
     */
    void update(String serverDir, List<String> serverFilesList);
}
