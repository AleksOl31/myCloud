package ru.alexanna.cloud.client.model;

import java.util.List;

public interface Observer {
    /**
     * Updates the server's current folder and file list after a notification is received
     * from an object that implements the Observable interface
     * @param serverDir current server folder
     *
     * @param serverFilesList list of files in the current folder
     */
    void updateServerSideState(String serverDir, List<String> serverFilesList);
}
