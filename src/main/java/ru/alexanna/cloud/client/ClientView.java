package ru.alexanna.cloud.client;


import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ru.alexanna.cloud.client.viewmodel.ClientViewModel;

public class ClientView implements Initializable/*, MessageListener*/ {

    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;

    private final ClientViewModel viewModel = new ClientViewModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            bindViewModel();
            initMouseListeners();
    }

    private void bindViewModel() {
        clientPath.textProperty().bind(viewModel.clientDirProperty());
        clientView.itemsProperty().bind(viewModel.clientFilesProperty());
        serverPath.textProperty().bind(viewModel.serverDirProperty());
        serverView.itemsProperty().bind(viewModel.serverFilesProperty());
    }

    private void initMouseListeners() {

        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewModel.changeClientDir(clientView.getSelectionModel().getSelectedItem());
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // todo Home Work
                viewModel.changeServerDir(serverView.getSelectionModel().getSelectedItem());
            }
        });

    }

/*    @Override
    public void onMessageReceived(CloudMessage message) {
        switch (message.getType()) {
            case FILE:
                processFileMessage((FileMessage) message);
                break;
            case LIST:
                processListMessage((ListFilesMessage) message);
                break;
            case PATH_CHANGE_REQUEST:
                // Temporary
                serverPath.setText(((PathChangeRequestMessage) message).getDirName());
                break;
        }
    }*/

/*    private void processFileMessage(FileMessage message) {
        try {
            Files.write(clientDir.resolve(message.getFileName()), message.getBytes());
            Platform.runLater(this::updateClientView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
//        connection.sendMessage(new FileMessage(clientDir.resolve(fileName)));
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
//        connection.sendMessage(new FileRequestMessage(fileName));
    }

    public void goToTopClientFolder(ActionEvent actionEvent) {
        viewModel.goToTopClientFolder();
    }

    public void goToTopServerFolder(ActionEvent actionEvent) {
        viewModel.goToTopServerFolder();
    }
}
