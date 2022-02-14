package ru.alexanna.cloud.client.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.alexanna.cloud.client.model.ClientSideModel;
import ru.alexanna.cloud.client.model.Observer;
import ru.alexanna.cloud.client.model.ServerSideModel;
import ru.alexanna.cloud.client.model.Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ClientViewModel implements Observer {

    private final StringProperty clientDir = new SimpleStringProperty("");
    private final ReadOnlyListProperty<String> clientFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty serverDir = new SimpleStringProperty("");
    private final ReadOnlyListProperty<String> serverFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ClientSideModel clientSideModel;
    private Server serverSideModel;

    public ClientViewModel() {
        initClientState();
        initServerState();
    }

    private void initClientState() {
        clientSideModel = new ClientSideModel(Paths.get(System.getProperty("user.home")));
        openClientDir();
    }

    private void openClientDir() {
        Platform.runLater( () -> {
            setClientDir(clientSideModel.getClientDir().toString());
            clientFiles.clear();
            clientFiles.addAll(clientSideModel.getClientFilesList().stream()
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList()));
            System.out.println(clientFiles);
        });
    }

    private void initServerState() {
        serverSideModel = new ServerSideModel();
        serverSideModel.registerObserver(this);
    }

    @Override
    public void update(String serverDir, List<String> serverFilesList) {
        Platform.runLater(() -> {
            setServerDir(serverDir);
            serverFiles.clear();
            serverFiles.addAll(serverFilesList.stream().sorted().collect(Collectors.toList()));
        });
    }

    public String getClientDir() {
        return clientDir.get();
    }

    public StringProperty clientDirProperty() {
        return clientDir;
    }

    public void setClientDir(String clientDir) {
        this.clientDir.set(clientDir);
    }

    public ObservableList<String> getClientFiles() {
        return clientFiles.get();
    }

    public ReadOnlyListProperty<String> clientFilesProperty() {
        return clientFiles;
    }

    public String getServerDir() {
        return serverDir.get();
    }

    public StringProperty serverDirProperty() {
        return serverDir;
    }

    public void setServerDir(String serverDir) {
        this.serverDir.set(serverDir);
    }

    public ObservableList<String> getServerFiles() {
        return serverFiles.get();
    }

    public ReadOnlyListProperty<String> serverFilesProperty() {
        return serverFiles;
    }

    public void changeClientDir(String selectedDir) {
        Path selectedPath = clientSideModel.getClientDir().resolve(selectedDir);
        if (Files.isDirectory(selectedPath)) {
            clientSideModel.setClientDir(selectedPath);
            openClientDir();
        }
    }

    public void goToTopClientFolder() {
        Path parentPath = clientSideModel.getClientDir().getParent();
        clientSideModel.setClientDir(parentPath);
        openClientDir();
    }

    public void upload(String fileName) {

    }

    public void changeServerDir(String selectedPath) {
        //PATH_CHANGE_REQUEST
        serverSideModel.changeCurrentDir(selectedPath);
    }

    public void goToTopServerFolder() {
        serverSideModel.changeCurrentDir("..");
    }
}





/*        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Platform.runLater(() -> {
                            //ваш код

                        });
                        return null;
                    }
                };
            }
        };
        service.start();*/