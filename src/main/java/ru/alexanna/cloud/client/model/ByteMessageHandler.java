package ru.alexanna.cloud.client.model;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.Command;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ByteMessageHandler implements MessageListener, Command {

    private final CloudServer serverSideModel;
    private final DataInputStream is;
//    private final FileCommand fileCommander;

    public ByteMessageHandler(CloudServer serverSideModel) {
        this.serverSideModel = serverSideModel;
        is = serverSideModel.getConnection().getIs();
//        fileCommander = new FileCommandExecutor();
    }

    @Override
    public void onMessageReceived(byte message) throws IOException {
        switch (message) {
            case POST_FILE:
                processGetFile();
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
        log.debug("Server received {} bytes", sendCompleted);
    }

    private void processGetFile() throws IOException {
        String fileName = is.readUTF();
        long fileSize = is.readLong();
        log.debug("Receive file: {}, size - {} bytes", fileName, fileSize);

        serverSideModel.writeFile(fileName, fileSize);
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
