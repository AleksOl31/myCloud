package ru.alexanna.cloud.io.general;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileCommand {
    byte AUTH = 10;
    byte GET_LIST = 31;
    byte POST_FILE = 32;
    byte GET_FILE = 33;
    byte CHANGE_PATH_REQUEST = 34;
    byte GET_OK = 0;
    byte GET_FAILED = 113;
    byte GET_BAD_CRED = 13;

    void upload();
    void download();
    void setHomeDir(String userHomeDir);
    List<String> getCurrentFilesList() throws IOException;
    void writeFile(String fileName, long fileSize, InputStream is) throws IOException;
}
