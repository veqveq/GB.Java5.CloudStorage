package com.veqveq.cloud.common;

import java.nio.file.Path;

public class Command extends BaseMessage {

    private enum BaseCommand{
        DELETE,RENAME,NEW
    }


    private BaseCommand command;
    private Path path;
    private String value;

    public Command(Path path, BaseCommand command) {
        this.command = command;
        this.path = path;
    }

    public Command(Path path, BaseCommand command, String value){
        this.value = value;
        this.path = path;
        this.command = command;
    }

    public BaseCommand getCommand() {
        return command;
    }

    public Path getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }
}
