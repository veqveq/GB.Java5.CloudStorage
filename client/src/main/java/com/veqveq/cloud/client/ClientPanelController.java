package com.veqveq.cloud.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
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
    public void btnUpperKey(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    @Override
    public void btnNew(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание каталога");
        dialog.setHeaderText("Введите название каталога");

        Optional<String> text = dialog.showAndWait();
        Path path = Paths.get(pathField.getText()).resolve(text.get());
        new File(path.toString()).mkdir();
        updateList(Paths.get(pathField.getText()));
    }

    @Override
    public void btnRename(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Выберите файл!").showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Переименование");
            dialog.setHeaderText("Введите новое имя файла");

            Optional<String> text = dialog.showAndWait();
            Path newPath = Paths.get(pathField.getText()).resolve(text.get());
            Path oldPath = Paths.get(pathField.getText()).resolve(file.getName());
            try {
                Files.copy(oldPath, newPath);
                Files.delete(oldPath);
                updateList(Paths.get(pathField.getText()));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Не удалось переименовать файл!").showAndWait();
            }
        }
    }

    @Override
    public void btnDelete(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Выберите файл!").showAndWait();
        } else {
            Path path = Paths.get(pathField.getText()).resolve(file.getName());
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
            } finally {
                updateList(Paths.get(pathField.getText()));
            }
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

    @Override
    protected Path getPathOnMouseClick() {
        Path path = Paths.get(pathField.getText()).resolve(clientFiles.getSelectionModel().getSelectedItem().getName());
        return path;
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
    public void btnCopy(ActionEvent actionEvent) {
    }

    @Override
    public void btnMove(ActionEvent actionEvent) {
    }
}
