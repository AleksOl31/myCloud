package ru.alexanna.cloud.client.model.connection;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.MessageListener;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Slf4j
public class CloudConnection {

    private DataInputStream is;
    private DataOutputStream os;
    private MessageListener listener;
    private Socket socket;

    public CloudConnection(String host, int port) {
        try {
            socket = new Socket(host, port);
            os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            log.debug("Network created... Output stream size {}", os.size());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
            log.debug("Read thread started: {}", readThread);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    // read from network
    private void readLoop() {
        try {
            while (true) {
                log.debug("Waiting for a message from the server...");
                byte message = is.readByte();
                log.debug("Message received: {}", message);
                listener.onMessageReceived(message);
            }
        } catch (IOException e) {
            log.error("Error reading data from the server.");
            e.printStackTrace();
        }
    }

    public void addMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void sendMessage(byte message) {
        log.debug(String.valueOf(new Date()));
        try {
            os.writeByte(message);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(Path fileName) {
        final int BUFFER_SIZE = 8192;
        try (FileInputStream fin = new FileInputStream(fileName.toString())){
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            byte[] bytes = new byte[BUFFER_SIZE];
            long size = Files.size(fileName);
            int sizePart = (int) size / BUFFER_SIZE;
            if (size % BUFFER_SIZE != 0) sizePart++;
            while (size > 0) {
                int i = fin.read(bytes);
                bos.write(bytes, 0, i);
                size -= i;
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCredentials() {
        try {
            os.writeByte(FileCommand.DO_AUTH);
            os.writeUTF("test_User !@#$$%^&*()_-+?||}{{}");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getOs() {
        return os;
    }

    public DataInputStream getIs() {
        return is;
    }
}
