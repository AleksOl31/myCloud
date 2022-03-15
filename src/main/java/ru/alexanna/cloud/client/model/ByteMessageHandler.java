package ru.alexanna.cloud.client.model;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.Command;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ByteMessageHandler implements MessageListener, Command {

    private final CloudServer serverSideModel;
//    private final DataOutputStream os;
    private final DataInputStream is;

    public ByteMessageHandler(CloudServer serverSideModel) {
        this.serverSideModel = serverSideModel;
//        os = serverSideModel.getConnection().getOs();
        is = serverSideModel.getConnection().getIs();
    }

    @Override
    public void onMessageReceived(byte message) throws IOException {
        switch (message) {
            case POST_FILE:
                processPostFile();
                break;
            case GET_FILES_LIST:
                processGetFilesList();
                break;
            case AUTH_OK:
            case GET_OK:
                serverSideModel.sendCommand(GET_FILES_LIST);
                break;
            case POST_COMPLETED:
                processPostCompleted();
                break;
            case GET_BAD_CRED:
                log.error("Bad credentials!");
                break;
        }
    }

    private void processPostCompleted() throws IOException {
        long sendCompleted = is.readLong();
        log.debug(String.valueOf(sendCompleted));
    }

    private void processPostFile() {

    }

    private void processGetFilesList() throws IOException {
        serverSideModel.setServerDir(is.readUTF());
        int fileNum = is.readInt();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < fileNum; i++) {
            String fileName = is.readUTF();
            files.add(fileName);
        }
        serverSideModel.setServerFilesList(files);
    }

}
