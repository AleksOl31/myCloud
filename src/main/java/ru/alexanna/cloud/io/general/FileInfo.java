package ru.alexanna.cloud.io.general;

import java.io.InputStream;

public class FileInfo {
    private String fileName;
    private long size;
    private InputStream fis;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public InputStream getFis() {
        return fis;
    }

    public void setFis(InputStream fis) {
        this.fis = fis;
    }
}
