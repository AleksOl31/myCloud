package ru.alexanna.cloud.io.general;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

public class FileCommandExecutor implements FileCommand {
    private final int BUFFER_SIZE = 8192;
    private Path cloudRootDir = Paths.get("data");
    private Path homeDir;
    private Path currentDir;
    private LongConsumer bytesWritten;

    public FileCommandExecutor() {
    }

    public FileCommandExecutor(Path cloudRootDir) {
//        this.homeDir = cloudRootDir.resolve(userHomeDir);
//        currentDir = homeDir;
        this.cloudRootDir = cloudRootDir;
        homeDir = cloudRootDir;
    }

    @Override
    public void upload() {

    }

    @Override
    public void download() {

    }

    @Override
    public void initHomeDir(String userHomeDir) {
        homeDir = cloudRootDir.resolve(userHomeDir);
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
        return currentDir.toString(); //homeDir != currentDir ? currentDir.toString() : "";
    }

    @Override
    public void setCurrentDir(String targetDir) {
        Path targetPath = currentDir.resolve(targetDir);
        if (Files.isDirectory(targetPath)) {
            if (!(currentDir.endsWith(homeDir) && targetDir.equals("..")))
                currentDir = targetPath.normalize();
        }
    }

    @Override
    public List<String> getCurrentFilesList() throws IOException {
        return Files.list(currentDir)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void writeFile(String fileName, long fileSize, InputStream is, LongConsumer bytesWritten) throws IOException {
        this.bytesWritten = bytesWritten;
        try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(currentDir.resolve(fileName).toString()))) {
            byte[] buf = new byte[BUFFER_SIZE];
            long totalRead = 0;
            while (totalRead < fileSize) {
                int readBytes = is.read(buf);
                totalRead += readBytes;
                fos.write(buf, 0, readBytes);
                onWriteCompleted(totalRead);
            }
        }
    }

    private void onWriteCompleted(long totalWritten) {
        bytesWritten.accept(totalWritten);
    }

    @Override
    public void sendFileToClient(DataOutputStream os, String fileName) {
        Path filePath = currentDir.resolve(fileName).toAbsolutePath();

        try (InputStream fin = new BufferedInputStream(new FileInputStream(filePath.toFile()))){
            byte[] bytes = new byte[BUFFER_SIZE];
            long size = Files.size(filePath);
            os.writeByte(Command.POST_FILE);
            os.writeUTF(fileName);
            os.writeLong(size);
            while (size > 0) {
                int numBytes = fin.read(bytes);
                os.write(bytes, 0, numBytes);
                size -= numBytes;
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
