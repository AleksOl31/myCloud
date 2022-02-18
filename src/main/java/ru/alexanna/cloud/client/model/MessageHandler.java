package ru.alexanna.cloud.client.model;

import ru.alexanna.cloud.MessageListener;
import ru.alexanna.cloud.model.CloudMessage;
import ru.alexanna.cloud.model.ListFilesMessage;

public class MessageHandler implements MessageListener {

    private final ServerSideModel serverSideModel;

    public MessageHandler(ServerSideModel serverSideModel) {
        this.serverSideModel = serverSideModel;
    }

    @Override
    public void onMessageReceived(CloudMessage message) {
        switch (message.getType()) {
            case FILE:
//                processFileMessage((FileMessage) message);
                break;
            case LIST:
                processListMessage((ListFilesMessage) message);
                break;
        }
    }

    private synchronized void processListMessage(ListFilesMessage message) {
        serverSideModel.setServerDir(message.getPath());
        serverSideModel.setServerFilesList(message.getFiles());
        serverSideModel.notifyObservers();
    }

}
