package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.CommandType;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServerHandlerIO implements Runnable {

    private static final int SIZE = 8192 * 2;

    private Path clientDir;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket incomingSocket;
    private byte[] buf;

    public ServerHandlerIO(Socket incomingSocket) {
        this.incomingSocket = incomingSocket;
        try {
            is = new DataInputStream(incomingSocket.getInputStream());
            os = new DataOutputStream(incomingSocket.getOutputStream());
            buf = new byte[SIZE];
            clientDir = Paths.get("data");
            sendServerFilesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte command = is.readByte();
//                log.debug("{} command received", command);
                processMessage(command);
            }
        } catch (IOException e) {
            log.debug("Client {} disconnected...", incomingSocket.getInetAddress());
        } finally {
            try {
                os.close();
                is.close();
                incomingSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(byte msg) {
        switch (msg) {
            case CommandType.LIST:
                sendServerFilesList();
                break;
            case CommandType.FILE:
                getFileFromClient();
                break;
        }
    }

    private void sendServerFilesList() {
        try {
            List<String> files = Files.list(clientDir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            os.writeByte(CommandType.LIST);
            os.writeInt(files.size());
            for (String file : files) {
                os.writeUTF(file);
            }
            os.flush();
            log.debug("List of files sent to client {} ", incomingSocket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFileFromClient() {
        try {
            String fileName = is.readUTF();
            log.debug("Received file {}", fileName);
            long size = is.readLong();
            try (OutputStream fos = new FileOutputStream(clientDir.resolve(fileName).toString())){
                for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {
                    int readBytes = is.read(buf);
                    fos.write(buf, 0 , readBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
