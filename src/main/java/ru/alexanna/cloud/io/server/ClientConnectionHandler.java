package ru.alexanna.cloud.io.server;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.FileCommand;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ClientConnectionHandler implements Runnable {

    private static final int BUFFER_SIZE = 8192;

    private Path clientDir;
    private DataInputStream is;
    private DataOutputStream os;
    private final Socket incomingSocket;
    private final ClientMsgProcessor msgProcessor;

    public ClientConnectionHandler(Socket incomingSocket) {
        this.incomingSocket = incomingSocket;
        this.msgProcessor = new ClientMsgProcessor();
        try {
            is = new DataInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(incomingSocket.getOutputStream()));

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
                log.debug("Entering a Thread Execution Loop");
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
            case FileCommand.GET_LIST:
                sendServerFilesList();
                break;
            case FileCommand.POST_FILE:
                getFileFromClient();
                break;
        }

    }

    private void sendServerFilesList() {
        try {
            List<String> files = Files.list(clientDir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            os.writeByte(FileCommand.GET_LIST);
            os.writeInt(files.size());
            for (String file : files) {
                os.writeUTF(file);
            }
            os.flush();
            log.debug("List of files sent to the client {} ", incomingSocket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGetOk() {
        try {
            os.writeByte(FileCommand.GET_OK);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Commit on GitHub 27.02.22
    private void getFileFromClient() {
        try {
            log.debug("Start file transfer {}", new Date());
            byte[] buf = new byte[BUFFER_SIZE];
            String fileName = is.readUTF();
            log.debug("Received file: {}", fileName);
            long fileSize = is.readLong();
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(clientDir.resolve(fileName).toString()))) {
                int countIterations = 0;
                long totalRead = 0;
                while (totalRead < fileSize) {
                    countIterations++;
                    int readBytes = is.read(buf);
                    totalRead += readBytes;
//                    if (countIterations % 10_000 == 0) log.debug("{} bytes read", totalRead);
                    fos.write(buf, 0, readBytes);
                }
                log.debug("Stop file transfer {}", new Date());
                sendGetOk();
            }
        } catch (Exception e) {
            log.error("Exception in getFileFromClient");
            e.printStackTrace();
        }
    }
}
