package ru.alexanna.cloud.io.general;

public interface Command {
    byte DO_AUTH = 10;
    byte AUTH_OK = 11;
    byte GET_BAD_CRED = 13;
    byte GET_FILES_LIST = 31;
    byte POST_FILE = 32;
    byte POST_COMPLETED = 33;
    byte GET_FILE = 34;
    byte CHANGE_PATH_REQUEST = 35;
    byte GET_OK = 0;
    byte GET_FAILED = 23;
    byte QUIT = 100;
    byte BYE = 101;
}
