package com.veqveq.cloud.common;


import java.nio.file.Path;

public class FileRequest extends BaseMessage {
    private Path path;

    public FileRequest(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
