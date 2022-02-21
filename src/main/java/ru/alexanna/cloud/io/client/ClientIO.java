package ru.alexanna.cloud.io.client;

import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.io.CommandType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ClientIO {

    private DataInputStream is;
    private DataOutputStream os;
    private Path currentDir;


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
//                log.debug("Response from server - {}", serverCmd);
                processServerCommand(serverCmd);
            }
        } catch (IOException /*| InterruptedException*/ e) {
            e.printStackTrace();
        }
    }

    private void processServerCommand(byte serverCmd) {
        switch (serverCmd) {
            case CommandType.LIST:
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

    private void sendFileToServer(Path fileName) {
        sendMsg(CommandType.FILE);
        try {
            os.writeUTF(fileName.getFileName().toString());
            long size = Files.size(fileName);
            byte[] bytes = Files.readAllBytes(fileName);
            os.writeLong(size);
            os.write(bytes);
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
            processClientCommand(client, msg);
        }
    }

    private static void processClientCommand(ClientIO client, byte command) {
        if (command == 21) return;
        switch (command) {
            case CommandType.FILE:
                log.debug(new Date().toString());
                client.sendFileToServer(client.currentDir.resolve("Tehnologiya_soloda_i_piva.pdf"));
                break;
            case CommandType.LIST:
                client.sendMsg(CommandType.LIST);
                break;
        }
    }

}
