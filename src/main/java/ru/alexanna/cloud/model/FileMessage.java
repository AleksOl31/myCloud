package ru.alexanna.cloud.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.Data;

@Data
public class FileMessage implements CloudMessage {

    private final String fileName;
    private final long fileSize;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        fileSize = Files.size(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE;
    }
}
