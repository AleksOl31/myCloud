package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.Command;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.*;
import java.util.List;

@Slf4j
public class ClientMsgProcessor implements Command {

    private final ClientConnectionHandler handler;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final FileCommand fileCommander;
    private boolean isAuthenticated = false;
    private String userName;

    public ClientMsgProcessor(ClientConnectionHandler handler, FileCommand fileCommander) {
        is = handler.getIs();
        os = handler.getOs();
        this.handler = handler;
        this.fileCommander = fileCommander;
    }

    public void commandProcessing(byte command) throws IOException {
        if (isAuthenticated)
            switch (command) {
                case GET_FILES_LIST:
                    sendServerFilesList();
                    break;
                case POST_FILE:
                    getFileFromClient();
                    sendCommand(GET_OK);
                    break;
                case CHANGE_PATH_REQUEST:
                    changePathRequestProcess();
                    break;
                case QUIT:
                    sendCommand(BYE);
                    handler.logout();
                    break;
            }
        else if (command == DO_AUTH) doAuthentication();
        else throw new IOException("Unexpected data from client");
    }

    private void doAuthentication() throws IOException {
        AuthenticationService authService = new AuthenticationService();
        String inboundMsg = is.readUTF();
        String[] credentials = inboundMsg.split("\\s");
        isAuthenticated = authService.findUser(credentials[0], credentials[1]);
        if (isAuthenticated) {
            this.userName = credentials[0];
            fileCommander.initHomeDir(userName);
            log.debug("User with username '{}' authenticated.", userName);
            sendCommand(AUTH_OK);
        } else {
            sendCommand(GET_BAD_CRED);
        }
    }

    private void sendServerFilesList() {
        try {
            List<String> files = fileCommander.getCurrentFilesList();
            os.writeByte(GET_FILES_LIST);
            os.writeUTF(fileCommander.getCurrentDir());
            os.writeInt(files.size());
            for (String file : files) {
                os.writeUTF(file);
            }
            os.flush();
            log.debug("List of files sent to the client {} with username '{}'", handler.getIncomingSocket().getInetAddress(), userName);
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

    private void changePathRequestProcess() throws IOException {
        String requestedDir = is.readUTF();
        fileCommander.setCurrentDir(requestedDir);
        log.debug(fileCommander.getCurrentDir());
        sendServerFilesList();
    }

    public String getUserName() {
        return userName;
    }

}
