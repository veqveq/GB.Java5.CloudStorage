package com.veqveq.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirMessage extends BaseMessage {
    private String [] dirs;
    private String path;

    public DirMessage(Path path) {
        Path p = path;
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < p.getNameCount(); i++) {
            sb.append(path.getName(i)).append("\\");
        }
        this.path = sb.toString();
        sb.setLength(0);
        try {
            dirs = Files.list(path).map(str -> str.toString()).toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getDirs() {
        return dirs;
    }

    public String getPath() {
        return path;
    }
}
