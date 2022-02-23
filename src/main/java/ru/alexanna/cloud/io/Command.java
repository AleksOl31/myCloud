package ru.alexanna.cloud.io;

public interface Command {
    byte GET_LIST = 31;
    byte POST_FILE = 32;
    byte GET_FILE = 33;
    byte CHANGE_PATH_REQUEST = 34;
}
