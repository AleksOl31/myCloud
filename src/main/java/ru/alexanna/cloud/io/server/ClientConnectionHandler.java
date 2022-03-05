package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.FileCommandExecutor;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ClientConnectionHandler implements Runnable {
    private DataInputStream is;
    private DataOutputStream os;
    private final Socket incomingSocket;
    private final ClientMsgProcessor msgProcessor;

    public ClientConnectionHandler(Socket incomingSocket) {
        this.incomingSocket = incomingSocket;
        try {
            is = new DataInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(incomingSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.msgProcessor = new ClientMsgProcessor(this, new FileCommandExecutor());
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte command = is.readByte();
                msgProcessor.commandProcessing(command);
            }
        } catch (IOException e) {
            log.debug("Client {}, with username `{}` disconnected. Exception: {}",
                    incomingSocket.getInetAddress(), msgProcessor.getUserName(), e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            os.close();
            is.close();
            incomingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getIncomingSocket() {
        return incomingSocket;
    }

    public DataOutputStream getOs() {
        return os;
    }

    public DataInputStream getIs() {
        return is;
    }
}
