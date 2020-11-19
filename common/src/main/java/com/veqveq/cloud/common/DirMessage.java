package com.veqveq.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DirMessage extends BaseMessage {
    private Stream<Path> directories;
    private Path path;

    public DirMessage(Path path) {
        this.path = path;
        try {
            directories = Files.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stream<Path> getDirectories() {
        return directories;
    }

    public Path getPath() {
        return path;
    }
}
