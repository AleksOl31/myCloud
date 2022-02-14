package ru.alexanna.cloud.client.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ClientSideModel {

    private Path clientDir;
    private List<Path> clientFilesList;

    public ClientSideModel(Path clientDir) {
        setClientDir(clientDir);
    }

    public Path getClientDir() {
        return clientDir;
    }

    public void setClientDir(Path clientDir) {
        try {
            this.clientDir = clientDir;
            setClientFilesList(Files.list(clientDir).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Path> getClientFilesList() {
        return clientFilesList;
    }

    public void setClientFilesList(List<Path> clientFilesList) {
        this.clientFilesList = clientFilesList;
    }
}
