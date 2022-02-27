package ru.alexanna.cloud.io.general;

public interface FileCommand {
    byte GET_LIST = 31;
    byte POST_FILE = 32;
    byte GET_FILE = 33;
    byte CHANGE_PATH_REQUEST = 34;
    byte GET_OK = 0;
    byte GET_FAILED = 113;

    void upload();
    void download();
}
