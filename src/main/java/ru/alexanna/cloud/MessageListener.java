package ru.alexanna.cloud;

import ru.alexanna.cloud.model.CloudMessage;

@FunctionalInterface
public interface MessageListener {

    void onMessageReceived(CloudMessage message);
}
