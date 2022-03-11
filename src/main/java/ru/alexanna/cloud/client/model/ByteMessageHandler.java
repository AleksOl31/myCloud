package ru.alexanna.cloud.client.model;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.MessageListener;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ByteMessageHandler implements MessageListener {

    private final ServerSideModel serverSideModel;
    private final DataOutputStream os;
    private final DataInputStream is;

    public ByteMessageHandler(ServerSideModel serverSideModel) {
        this.serverSideModel = serverSideModel;
        os = serverSideModel.getConnection().getOs();
        is = serverSideModel.getConnection().getIs();
        // TODO здесь временная аутентификация
//        serverSideModel.getConnection().sendCredentials();
    }

    @Override
    public void onMessageReceived(byte message) throws IOException {
        switch (message) {
            case FileCommand.POST_FILE:
                processPostFile();
                break;
            case FileCommand.GET_FILES_LIST:
                processGetFilesList();
                break;
            case FileCommand.AUTH_OK:
                serverSideModel.getConnection().sendMessage(FileCommand.GET_FILES_LIST);
                break;
            case FileCommand.GET_BAD_CRED:
                log.error("Bad credentials!");
                break;
        }
    }

    private void processPostFile() {

    }

    private synchronized void processGetFilesList(/*ListFilesMessage message*/) throws IOException {
        /*serverSideModel.setServerDir(message.getPath());
        serverSideModel.setServerFilesList(message.getFiles());
        serverSideModel.notifyObservers();*/

        serverSideModel.setServerDir(is.readUTF());
        int fileNum = is.readInt();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < fileNum; i++) {
            String fileName = is.readUTF();
            files.add(fileName);
        }
        serverSideModel.setServerFilesList(files);
        serverSideModel.notifyObservers();
    }

}
