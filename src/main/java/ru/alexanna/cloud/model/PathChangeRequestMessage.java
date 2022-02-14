package ru.alexanna.cloud.model;

import lombok.Data;

@Data
public class PathChangeRequestMessage implements CloudMessage {

    private final String dirName;

    public PathChangeRequestMessage(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_CHANGE_REQUEST;
    }
}
