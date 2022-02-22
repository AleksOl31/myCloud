package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.CommandType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServerHandlerIO implements Runnable {

    private static final int SIZE = 8192;

    private Path clientDir;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket incomingSocket;
    private byte[] buf;

    public ServerHandlerIO(Socket incomingSocket) {
        this.incomingSocket = incomingSocket;
        try {
            is = new DataInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
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
                log.debug("Start loop while of the Method run");
                byte command = is.readByte();
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
            case CommandType.GET_LIST:
                sendServerFilesList();
                break;
            case CommandType.POST_FILE:
                getFileFromClient();
                break;
        }

    }

    private void sendServerFilesList() {
        try {
            List<String> files = Files.list(clientDir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            os.writeByte(CommandType.GET_LIST);
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
            log.debug("Received file: {}", fileName);
            long fileSize = is.readLong();
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(clientDir.resolve(fileName).toString()))) {
                int count = 0;
                long totalRead = 0;
                while (totalRead < fileSize) {
                    count++;
                    int readBytes = is.read(buf);
                    totalRead += readBytes;
                    if (count % 1_000 == 0) log.debug("{} bytes read", totalRead);
                    fos.write(buf, 0 , readBytes);
                }
            }
        } catch (IOException e) {
            log.error("Exception in getFileFromClient");
            e.printStackTrace();
        }
    }
}
