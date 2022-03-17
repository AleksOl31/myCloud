package ru.alexanna.cloud.io.general;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.LongConsumer;

public interface FileCommand {
    void upload();

    void download();

    void initHomeDir(String userHomeDir);

    Path getCurrentDir();

    void setCurrentDir(String targetDir);

//    void setCurrentDir(Path targetDir);

    List<String> getCurrentFilesList() throws IOException;

    void writeFile(String fileName, long fileSize, InputStream is, LongConsumer bytesWritten) throws IOException;

    void sendFileToClient(DataOutputStream os, String fileName);

}
