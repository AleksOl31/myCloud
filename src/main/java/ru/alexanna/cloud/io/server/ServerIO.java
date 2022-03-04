package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class ServerIO {
    private final int PORT = 8189;

    public ServerIO() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket incoming = server.accept();
                log.debug("New client connected {} ", incoming.getInetAddress());
                new Thread(new ClientConnectionHandler(incoming)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
         new ServerIO();
    }
}
