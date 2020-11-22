package com.veqveq.cloud.client.controllers;

import com.veqveq.cloud.client.utils.FileInfo;
import com.veqveq.cloud.client.utils.Network;
import com.veqveq.cloud.common.Command;
import com.veqveq.cloud.common.FileMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientPanelController extends BasePanelController {

    @FXML
    ComboBox<String> diskSelector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDiskSelector();
        updateList(Paths.get(diskSelector.getSelectionModel().getSelectedItem()));
        super.initialize(location, resources);
    }

    @Override
    protected void fileCreate(Path path) {
        new File(path.toString()).mkdir();
    }

    @Override
    protected void fileDelete(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Файл не удален!").showAndWait();
        }
    }

    @Override
    protected void fileRename(Path oldName, Path newName) {
        try {
            Files.copy(oldName, newName);
            Files.delete(oldName);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось переименовать файл!").showAndWait();
        }
    }

    @Override
    protected void updateList(Path path) {
        pathField.setText(path.normalize().toAbsolutePath().toString());
        try {
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            clientFiles.sort();
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, String.format("Нет доступа к директории %s", path), ButtonType.OK).showAndWait();
        }
    }

    public void btnSelectDisk(ActionEvent actionEvent) {
        ComboBox<String> event = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(event.getSelectionModel().getSelectedItem()));
    }

    private void initDiskSelector() {
        diskSelector.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            diskSelector.getItems().add(path.toString());
        }
        diskSelector.getSelectionModel().select(0);
    }

    @Override
    public void btnCopy(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        if (Files.isDirectory(srcPath)) {
            int parentLength = srcPath.getNameCount();
            try {
                Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (Files.list(dir).collect(Collectors.toList()).size() == 0) {
                            Path path = dstPath.resolve(dir.subpath(0, dir.getNameCount() - parentLength)).resolve((dir).getFileName());
                            Network.sendMsg(new Command(path, Command.BaseCommand.NEW));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path path = dstPath.resolve(file.subpath(0, file.getNameCount() - parentLength));
                        Network.sendMsg(new FileMessage(file, path));
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.printf("[Клиент]:%s -> %s [Облако]\n", srcPath.toString(), dstPath.toString());
            Network.sendMsg(new FileMessage(srcPath, dstPath));
        }
        updatedPanel.updateList(dstPath);
    }

    @Override
    public void btnMove(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        System.out.printf("[Клиент]:%s -> %s [Облако]\n", srcPath.toString(), dstPath.toString());
        btnCopy(srcPath, dstPath, updatedPanel);
        fileDelete(srcPath);
        updateList(srcPath.getParent());
        updatedPanel.updateList(dstPath);
    }
}
