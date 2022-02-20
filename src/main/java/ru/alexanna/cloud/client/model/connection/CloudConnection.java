package ru.alexanna.cloud.client.model.connection;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.MessageListener;
import ru.alexanna.cloud.model.CloudMessage;
import ru.alexanna.cloud.model.FileMessage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Slf4j
public class CloudConnection {

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private MessageListener listener;
    private Socket socket;

    public CloudConnection(String host, int port) {
        try {
            socket = new Socket(host, port);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
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
                CloudMessage message = (CloudMessage) is.readObject();
                log.debug("Message received: {}", message);
                listener.onMessageReceived(message);
            }
        } catch (Exception e) {
            log.error("Error reading data from the server.");
            e.printStackTrace();
        }
    }

    public void addMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void sendMessage(CloudMessage message) {
        log.debug(String.valueOf(new Date()));
        try {
            os.writeObject(message);
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
}
