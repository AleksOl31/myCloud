package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class CloudServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8189);
        while (true) {
            Socket incoming = server.accept();
            log.debug("New client connected {} ", incoming.getInetAddress());
            new Thread(new ClientConnectionHandler(incoming)).start();
        }
    }
}
