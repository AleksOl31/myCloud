package ru.alexanna.cloud.model;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    CommandType getType();
}
