package ru.alexanna.cloud.io.client;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.Command;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

@Slf4j
public class ClientIO {

    private DataInputStream is;
    private DataOutputStream os;
    private Path currentDir;
    private final int BUFFER_SIZE = 8192;


    public ClientIO() {
        try {
            currentDir = Paths.get(System.getProperty("user.home"));
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLoop() {
        try {
            while (true) {
                byte serverCmd = is.readByte();
                processServerCommand(serverCmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processServerCommand(byte serverCmd) {
        switch (serverCmd) {
            case Command.GET_LIST:
                processServerFilesList();
        }
    }

    private void processServerFilesList() {
        try {
            int fileNum = is.readInt();
            for (int i = 0; i < fileNum; i++) {
                String fileName = is.readUTF();
                log.info(fileName);
            }
            System.out.print("Enter the command: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommandType(byte commandType, boolean autoFlush) {
        try {
            os.write(commandType);
            if (autoFlush) os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileToServer(String fileName) {
        Path clientDir = Paths.get(System.getProperty("user.home"));
        String file = clientDir.resolve(fileName).toAbsolutePath().toString();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            long size = Files.size(clientDir.resolve(fileName).toAbsolutePath());
            sendCommandType(Command.POST_FILE, false);
            os.writeUTF(fileName);
            os.writeLong(size);
            int percentageSent = 0;
            long bytesTransferred = 0;
            while (bytesTransferred < size) {
                int numBytes = fis.read(bytes);
                os.write(bytes, 0, numBytes);
                bytesTransferred += numBytes;
                percentageSent = (int) (bytesTransferred * 100 / size);
            }
            os.flush();
            System.out.print("Enter the command: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Test for commit on GitHub 23.02.22
    public static void main(String[] args) {
        ClientIO client = new ClientIO();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextByte()) {
            byte msg = scanner.nextByte();

            if (msg == 21) return;

            switch (msg) {
                case Command.POST_FILE:
                    log.debug(new Date().toString());
                    client.sendFileToServer("!!!Big_file_for_test_transfer");
                    break;
                case Command.GET_LIST:
                    client.sendCommandType(Command.GET_LIST, true);
                    break;
            }

        }
    }

}
