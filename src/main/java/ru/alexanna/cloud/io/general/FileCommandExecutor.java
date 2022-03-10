package ru.alexanna.cloud.io.general;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileCommandExecutor implements FileCommand {
    private static final int BUFFER_SIZE = 8192;
    private final Path cloudDataDir = Paths.get("data");
    private Path homeDir;
    private Path currentDir;

    public FileCommandExecutor() {
    }

    public FileCommandExecutor(String userHomeDir) {
        this.homeDir = cloudDataDir.resolve(userHomeDir);
        currentDir = homeDir;
    }

    @Override
    public void upload() {

    }

    @Override
    public void download() {

    }

    @Override
    public void initHomeDir(String userHomeDir) {
        homeDir = cloudDataDir.resolve(userHomeDir);
        try {
            if (Files.notExists(homeDir)) {
                Files.createDirectory(homeDir);
            }
            currentDir = homeDir;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCurrentDir() {
        return currentDir.toString();
    }

    @Override
    public List<String> getCurrentFilesList() throws IOException {
        return Files.list(currentDir)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void writeFile(String fileName, long fileSize, InputStream is) throws IOException {
        OutputStream fos = new BufferedOutputStream(new FileOutputStream(currentDir.resolve(fileName).toString()));
        int countIterations = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        long totalRead = 0;
        while (totalRead < fileSize) {
            countIterations++;
            int readBytes = is.read(buf);
            totalRead += readBytes;
//                    if (countIterations % 10_000 == 0) log.debug("{} bytes read", totalRead);
            fos.write(buf, 0, readBytes);
        }
    }
}
