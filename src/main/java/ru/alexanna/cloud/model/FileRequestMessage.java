package ru.alexanna.cloud.model;

import lombok.Data;

@Data
public class FileRequestMessage implements CloudMessage {

    private final String fileName;

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
