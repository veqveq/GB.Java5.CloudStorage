package com.veqveq.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessage extends BaseMessage{
    private String path;
    private String name;
    private byte[] data;

    public FileMessage(Path path) {
        this.path = path.toString();
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
        return Paths.get(path);
    }

    public byte[] getData() {
        return data;
    }
}
