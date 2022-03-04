package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.*;
import java.util.List;

@Slf4j
public class ClientMsgProcessor {

    private ClientConnectionHandler handler;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final FileCommand fileCommander;

    public ClientMsgProcessor(ClientConnectionHandler handler, FileCommand fileCommander) {
       is = handler.getIs();
       os = handler.getOs();
       this.handler = handler;
       this.fileCommander = fileCommander;
    }

    public void commandProcessing(byte command) {
        switch (command) {
            case FileCommand.AUTH:
                doAuthentication();
                break;
            case FileCommand.GET_LIST:
                sendServerFilesList();
                break;
            case FileCommand.POST_FILE:
                getFileFromClient();
                sendCommand(FileCommand.GET_OK);
                break;
        }
    }

    private void doAuthentication() {
        AuthenticationService authService = new AuthenticationService();
        try {
            String userName = is.readUTF();
            String password = is.readUTF();
            if (authService.findUser(userName, password)) {
                fileCommander.setHomeDir(userName);
                sendServerFilesList();
            } else {
                sendCommand(FileCommand.GET_BAD_CRED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendServerFilesList() {
        try {
            List<String> files = fileCommander.getCurrentFilesList();
            os.writeByte(FileCommand.GET_LIST);
            os.writeInt(files.size());
            for (String file : files) {
                os.writeUTF(file);
            }
            os.flush();
            log.debug("List of files sent to the client {} ", handler.getIncomingSocket().getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(byte command) {
        try {
            os.writeByte(command);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFileFromClient() {
        try {
            String fileName = is.readUTF();
            log.debug("Receive file: {}", fileName);
            long fileSize = is.readLong();
            fileCommander.writeFile(fileName, fileSize, is);
        } catch (IOException e) {
            log.error("Exception in getFileFromClient");
            e.printStackTrace();
        }
    }

}
