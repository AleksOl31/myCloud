package ru.alexanna.cloud.client.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ClientSideModel {

    private Path currentDir;
    private List<Path> clientFilesList;

    public ClientSideModel(Path currentDir) {
        setCurrentDir(currentDir);
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        try {
            this.currentDir = currentDir;
            setClientFilesList(Files.list(currentDir)
                    .filter(this::isHidden)
                    .sorted()
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isHidden(Path path) {
        boolean result;
        try {
            result = !Files.isHidden(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return result;
    }

    public List<Path> getCurrentFilesList() {
        return clientFilesList;
    }

    public void setClientFilesList(List<Path> clientFilesList) {
        this.clientFilesList = clientFilesList;
    }
}
