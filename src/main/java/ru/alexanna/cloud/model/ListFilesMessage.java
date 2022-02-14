package ru.alexanna.cloud.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ListFilesMessage implements CloudMessage {

    private final List<String> files;
    private final String path;

    public ListFilesMessage(Path path) throws IOException {
        this.path = path.normalize().toString();
        files = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }

}
