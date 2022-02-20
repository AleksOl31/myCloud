package ru.alexanna.cloud.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.Data;

@Data
public class FileMessage implements CloudMessage {

    private final String fileName;
//    private final long fileSize;
    private final byte[] bytes;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
//        fileSize = Files.size(path);
        bytes = Files.readAllBytes(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE;
    }
}
