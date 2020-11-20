package com.veqveq.cloud.client;

import com.veqveq.cloud.common.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CloudPanelController extends BasePanelController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        updateList(null);
        super.initialize(location, resources);
    }

    @Override
    protected void fileCreate(Path path) {
        Network.sendMsg(new Command(path, Command.BaseCommand.NEW));
    }

    @Override
    protected void fileRename(Path oldName, Path newName) {
        Network.sendMsg(new Command(oldName, newName, Command.BaseCommand.RENAME));
    }

    @Override
    protected void fileDelete(Path path) {
        Network.sendMsg(new Command(path, Command.BaseCommand.DELETE));
    }

    @Override
    public void btnCopy(ActionEvent actionEvent) {
    }

    @Override
    public void btnMove(ActionEvent actionEvent) {

    }

    @Override
    protected void updateList(Path path) {
        Network.sendMsg(new DirRequest(path));
        Thread t = new Thread(() -> {
            while (true) {
                DirMessage msg;
                try {
                    msg = (DirMessage) Network.readObject();
                    System.out.println(msg.getClass() + " получен");
                    pathField.setText(msg.getPath());
                    List<FileInfo> pathList = Arrays.stream(msg.getDirs())
                            .map(str -> Paths.get(str))
                            .map(FileInfo::new)
                            .collect(Collectors.toList());
                    clientFiles.getItems().clear();
                    for (FileInfo fileInfo : pathList) {
                        clientFiles.getItems().add(fileInfo);
                    }
                    clientFiles.sort();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        });
        t.start();
    }
}
