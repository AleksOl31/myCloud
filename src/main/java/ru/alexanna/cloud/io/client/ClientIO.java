package ru.alexanna.cloud.io.client;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.CommandType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
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
            case CommandType.GET_LIST:
                processServerFilesList();
        }
    }

    private void processServerFilesList() {
        try {
            int fileNum = is.readInt();
//            List<String> serverFilesList = new ArrayList<>(fileNum);
            for (int i = 0; i < fileNum; i++) {
                String fileName = is.readUTF();
//                serverFilesList.add(is.readUTF());
                log.info(fileName);
            }
            System.out.print("Enter the command: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(byte command) {
        try {
            os.write(command);
//            os.flush();
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
            System.out.println(size);
            sendMsg(CommandType.POST_FILE);
            os.writeUTF(fileName);
            os.writeLong(size);
            while (size > 0) {
                int numBytes = fis.read(bytes);
                os.write(bytes, 0, numBytes);
                size -= numBytes;
            }
            os.flush();
//            sendMsg(CommandType.LIST);
            log.debug(new Date().toString());
            System.out.print("Enter the command: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientIO client = new ClientIO();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextByte()) {
            byte msg = scanner.nextByte();

            if (msg == 21) return;

            switch (msg) {
                case CommandType.POST_FILE:
                    log.debug(new Date().toString());
                    client.sendFileToServer("kunce-v-tehnologiya-soloda-i-piva_f62d96649ae.pdf");
                    break;
                case CommandType.GET_LIST:
                    client.sendMsg(CommandType.GET_LIST);
                    break;
            }

        }
    }

    private static void processClientCommand(ClientIO client, byte command) {

    }

}
