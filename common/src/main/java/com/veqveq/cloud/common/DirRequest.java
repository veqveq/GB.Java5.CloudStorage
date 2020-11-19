package com.veqveq.cloud.common;


import java.nio.file.Path;

public class DirRequest extends BaseMessage{
    Path directory;

    public DirRequest(Path directory) {
        this.directory = directory;
    }

    public Path getDirectory() {
        return directory;
    }
}
