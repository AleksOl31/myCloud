package ru.alexanna.cloud.client.model.connection;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.MessageListener;
import ru.alexanna.cloud.model.CloudMessage;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class CloudConnection {

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private MessageListener listener;


    public CloudConnection(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            log.debug("Network created...");
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
        try {
            os.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
