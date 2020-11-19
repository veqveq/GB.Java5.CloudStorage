package com.veqveq.cloud.client;

import com.veqveq.cloud.common.DirMessage;
import com.veqveq.cloud.common.DirRequest;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudPanelController extends BasePanelController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateList(null);
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

    }

    @Override
    public void btnRename(ActionEvent actionEvent) {

    }

    @Override
    public void btnDelete(ActionEvent actionEvent) {

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
        try {
            DirMessage msg = (DirMessage) Network.readMsg();
            pathField.setText(msg.getPath().toString());
            List<FileInfo> pathList = msg.getDirectories().map(FileInfo::new).collect(Collectors.toList());
            clientFiles.getItems().clear();
            for (FileInfo fileInfo : pathList) {
                clientFiles.getItems().add(fileInfo);
            }
            clientFiles.sort();
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, String.format("Нет доступа к директории %s", path), ButtonType.OK).showAndWait();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Path getPathOnMouseClick() {
        return null;
    }
}
