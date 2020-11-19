package com.veqveq.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends BaseMessage{
    private Path path;
    private String name;
    private byte[] data;

    public FileMessage(Path path) {
        this.path = path;
        this.name = path.getFileName().toString();
        try {
            this.data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public byte[] getData() {
        return data;
    }
}
