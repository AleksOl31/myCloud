package ru.alexanna.cloud.io.client;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.general.FileCommand;

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
//            "192.168.50.114" - home address
//            "10.70.29.158" - work address
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
            case FileCommand.GET_LIST:
                processServerFilesList();
                break;
            case FileCommand.GET_OK:
                processCetOk();
                break;
            case FileCommand.GET_FAILED:
                processGetFailed();
                break;
            case FileCommand.GET_BAD_CRED:
                log.error("Bad credentials");
        }
    }

    private void processCetOk() {
        log.debug("File received at {}", new Date());
        System.out.print("Enter the command: ");
    }

    private void processGetFailed() {
        log.error("Failed to get file");
        System.out.print("Enter the command: ");
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
        log.debug("Start file transfer at {}", new Date());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            long size = Files.size(clientDir.resolve(fileName).toAbsolutePath());
            sendCommandType(FileCommand.POST_FILE, false);
            os.writeUTF(fileName);
            os.writeLong(size);
            int percentageSent;
            long bytesTransferred = 0;
            int count = 0;
            while (bytesTransferred < size) {
                count++;
                int numBytes = fis.read(bytes);
                os.write(bytes, 0, numBytes);
                bytesTransferred += numBytes;
                percentageSent = (int) (bytesTransferred * 100 / size);
                if (count % 10_000 == 0 || bytesTransferred == size)
                    log.debug("{} bytes wrote from {}. Current pack {} or {} percent", bytesTransferred, size, bytes.length, percentageSent);
            }
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendCredentials() {
        try {
            os.writeByte(FileCommand.AUTH);
            os.writeUTF("test_User");
            os.writeUTF("password");
            os.flush();
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
                case FileCommand.AUTH:
                    client.sendCredentials();
                    break;
                case FileCommand.POST_FILE:
                    client.sendFileToServer("!!!Big_file_for_test_transfer");
                    break;
                case FileCommand.GET_LIST:
                    client.sendCommandType(FileCommand.GET_LIST, true);
                    break;
            }

        }
    }

}
