package Client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    private String name;
    @FXML
    ComboBox<String> diskSelector;

    @FXML
    TextField pathField;

    @FXML
    TableView<FileInfo> clientFiles;

    @FXML
    Label panelName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name = panelName.getText();
        if (name.equals("Клиент")) {
            initDiskSelector();
            updateClientList(Paths.get(diskSelector.getSelectionModel().getSelectedItem()));
        } else {
            updateServerList(generatePath("root"));
        }
        initTableView();
        mouseListener();
    }

    public void btnUpperKey(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath.toString());
        }
    }

    public void btnSelectDisk(ActionEvent actionEvent) {
        ComboBox<String> event = (ComboBox<String>) actionEvent.getSource();
        updateClientList(Paths.get(event.getSelectionModel().getSelectedItem()));
    }

    public void btnNew(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание каталога");
        dialog.setHeaderText("Введите название каталога");

        Optional<String> text = dialog.showAndWait();
        Path path = generatePath(pathField.getText()).resolve(text.get());
        new File(path.toString()).mkdir();
        updateList(pathField.getText());
    }

    public void btnRename(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Выберите файл!").showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Переименование");
            dialog.setHeaderText("Введите новое имя файла");

            Optional<String> text = dialog.showAndWait();
            Path newPath = generatePath(pathField.getText()).resolve(text.get());
            Path oldPath = generatePath(pathField.getText()).resolve(file.getName());
            try {
                Files.copy(oldPath, newPath);
                Files.delete(oldPath);
                updateList(pathField.getText());
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Не удалось переименовать файл!").showAndWait();
            }
        }
    }

    public void btnDelete(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Выберите файл!").showAndWait();
        } else {
            Path path = generatePath(pathField.getText()).resolve(file.getName());
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
            }finally {
                updateList(pathField.getText());
            }
        }
    }

    private void initDiskSelector() {
        diskSelector.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            diskSelector.getItems().add(path.toString());
        }
        diskSelector.getSelectionModel().select(0);
    }

    private void initTableView() {
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
                    Path path = Paths.get(pathField.getText()).resolve(clientFiles.getSelectionModel().getSelectedItem().getName());
                    if (Files.isDirectory(generatePath(path.toString()))) {
                        updateList(path.toString());
                    }
                }
            }
        });
    }

    private void updateTableView(Path path) {
        try {
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            clientFiles.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, String.format("Нет доступа к директории %s", path), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private Path generatePath(String finalToken) {
        if (name.equals("Клиент")) {
            return Paths.get(pathField.getText()).resolve(finalToken);
        } else {
            return Paths.get("src\\main\\java\\Server").resolve(finalToken);
        }
    }

    private void updateList(String path) {
        if (name.equals("Клиент")) {
            updateClientList(Paths.get(path));
        } else {
            updateServerList(generatePath(path));
        }
    }

    private void updateClientList(Path path) {
        pathField.setText(path.normalize().toAbsolutePath().toString());
        updateTableView(path);
    }

    private void updateServerList(Path path) {
        StringBuilder text = new StringBuilder();
        for (int i = 4; i < path.getNameCount(); i++) {
            text.append(path.getName(i)).append("\\");
        }
        pathField.setText(text.toString());
        updateTableView(generatePath(text.toString()));
        text.setLength(0);
    }

    public void btnCopy(ActionEvent actionEvent) {
    }

    public void btnMove(ActionEvent actionEvent) {
    }
}
