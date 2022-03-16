package ru.alexanna.cloud.client;


import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ru.alexanna.cloud.client.viewmodel.ClientViewModel;

public class ClientView implements Initializable {

    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;
    public Label percentCompleted;

    private final ClientViewModel viewModel = new ClientViewModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            bindViewModel();
            initMouseListeners();

            viewModel.performAuthenticate();
    }

    private void bindViewModel() {
        clientPath.textProperty().bind(viewModel.clientDirProperty());
        clientView.itemsProperty().bind(viewModel.clientFilesProperty());
        serverPath.textProperty().bind(viewModel.serverDirProperty());
        serverView.itemsProperty().bind(viewModel.serverFilesProperty());
        percentCompleted.textProperty().bind(viewModel.percentCompletedProperty());
    }

    private void initMouseListeners() {

        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewModel.changeClientDir(clientView.getSelectionModel().getSelectedItem());
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewModel.changeServerDir(serverView.getSelectionModel().getSelectedItem());
            }
        });

    }

    public void upload(ActionEvent actionEvent) {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        viewModel.upload(fileName);
    }


    public void download(ActionEvent actionEvent) {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        viewModel.download(fileName);
    }

    public void goToTopClientFolder(ActionEvent actionEvent) {
        viewModel.goToTopClientFolder();
    }

    public void goToTopServerFolder(ActionEvent actionEvent) {
        viewModel.goToTopServerFolder();
    }
}
