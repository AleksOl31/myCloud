package ru.alexanna.cloud;

import ru.alexanna.cloud.model.CloudMessage;

import java.io.IOException;

@FunctionalInterface
public interface MessageListener {

    void onMessageReceived(byte message) throws IOException;
}
