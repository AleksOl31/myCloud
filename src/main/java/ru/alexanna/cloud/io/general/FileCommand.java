package ru.alexanna.cloud.io.general;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileCommand {
    void upload();
    void download();
    void initHomeDir(String userHomeDir);
    String getCurrentDir();
    List<String> getCurrentFilesList() throws IOException;
    void writeFile(String fileName, long fileSize, InputStream is) throws IOException;
}
