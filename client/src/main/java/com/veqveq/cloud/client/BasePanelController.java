package com.veqveq.cloud.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public abstract class BasePanelController implements Initializable {
    @FXML
    TextField pathField;

    @FXML
    TableView<FileInfo> clientFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableView();
        mouseListener();
    }

    public abstract void btnUpperKey(ActionEvent actionEvent);

    public abstract void btnNew(ActionEvent actionEvent);

    public abstract void btnRename(ActionEvent actionEvent);

    public abstract void btnDelete(ActionEvent actionEvent);

    public abstract void btnCopy(ActionEvent actionEvent);

    public abstract void btnMove(ActionEvent actionEvent);

    protected abstract void updateList(Path path);

    protected abstract Path getPathOnMouseClick();

    protected void initTableView() {
        TableColumn<FileInfo, String> fileType = new TableColumn<>("Тип");
        fileType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileType.setPrefWidth(50);

        TableColumn<FileInfo, String> fileName = new TableColumn<>("Имя");
        fileName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        fileName.setPrefWidth(250);

        TableColumn<FileInfo, Long> fileSize = new TableColumn<>("Размер");
        fileSize.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSize.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty || item == -1L) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%,d bytes", item));
                }
            }
        });
        fileSize.setPrefWidth(150);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        TableColumn<FileInfo, String> lastModified = new TableColumn<>("Изменен");
        lastModified.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        lastModified.setPrefWidth(150);

        clientFiles.getColumns().addAll(fileType, fileName, fileSize, lastModified);
        clientFiles.getSortOrder().add(fileType);
    }

    private void mouseListener() {
        clientFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = getPathOnMouseClick();
                    if (Files.isDirectory(path)) {
                        updateList(path);
                    }
                }
            }
        });
    }
}
