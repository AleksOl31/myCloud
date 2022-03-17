package ru.alexanna.cloud.client.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.alexanna.cloud.client.model.*;
import ru.alexanna.cloud.io.general.FileCommand;
import ru.alexanna.cloud.io.general.FileCommandExecutor;

import java.io.IOException;
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
    private final StringProperty percentCompleted = new SimpleStringProperty("test");
//    private ClientSideModel clientSideModel;
    // Изменения 16.03.22 - для перехода на FileCommand в кач-ве ClientSideModel
    private FileCommand clientSideModel;
    private CloudServer serverSideModel;

    public ClientViewModel() {
        initClientSideState();
        initServerSideState();
    }

    private void initClientSideState() {
//        clientSideModel = new ClientSideModel(Paths.get(System.getProperty("user.home")));
        clientSideModel = new FileCommandExecutor(Paths.get(System.getProperty("user.home")));
        updateClientSideState();
    }

    @Override
    public void updateClientSideState() {
        Platform.runLater(() -> {
            setClientDir(clientSideModel.getCurrentDir().toString());
            clientFiles.clear();
            try {
                clientFiles.addAll(clientSideModel.getCurrentFilesList()/*.stream()
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList())*/);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initServerSideState() {
        serverSideModel = new ServerSideModel(clientSideModel);
        serverSideModel.registerObserver(this);
    }

    @Override
    public void updateServerSideState(String serverDir, List<String> serverFilesList) {
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

    public String getPercentCompleted() {
        return percentCompleted.get();
    }

    public StringProperty percentCompletedProperty() {
        return percentCompleted;
    }

    public void setPercentCompleted(String percentCompleted) {
        this.percentCompleted.set(percentCompleted);
    }

    public void changeClientDir(String selectedDir) {

        Path selectedPath = clientSideModel.getCurrentDir().resolve(selectedDir);
        if (Files.isDirectory(selectedPath)) {
            clientSideModel.setCurrentDir(selectedPath.toString());
            updateClientSideState();
        }
    }

    public void goToTopClientFolder() {
        Path parentPath = clientSideModel.getCurrentDir().getParent();
        clientSideModel.setCurrentDir(parentPath.toString());
        updateClientSideState();
    }

    public void upload(String fileName) {
        Platform.runLater(() -> {
            Path path = clientSideModel.getCurrentDir().resolve(fileName).toAbsolutePath();
            serverSideModel.upload(path);
        });
    }

    public void download(String fileName) {
        Platform.runLater(() -> {
            serverSideModel.download(fileName);
        });
    }

    public void changeServerDir(String selectedPath) {
        //PATH_CHANGE_REQUEST
        serverSideModel.changeCurrentDir(selectedPath);
    }

    public void goToTopServerFolder() {
        serverSideModel.changeCurrentDir("..");
    }

    // TODO здесь временная аутентификация
    public void performAuthenticate() {
        serverSideModel.doAuthenticate("test_User", "!@#$$%^&*()_-+?||}{{}");
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