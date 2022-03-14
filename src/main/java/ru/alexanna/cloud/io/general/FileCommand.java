package ru.alexanna.cloud.io.general;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileCommand {
    void upload();

    void download();

    void initHomeDir(String userHomeDir);

    String getCurrentDir();

    void setCurrentDir(String targetDir);

    List<String> getCurrentFilesList() throws IOException;

    void writeFile(String fileName, long fileSize, InputStream is) throws IOException;

//    FileInfo openFile(Path fileName) throws IOException;
//
//    int readFile(FileInfo fileInfo) throws IOException;
//
//    void closeFile(FileInfo fileInfo) throws IOException;
}
