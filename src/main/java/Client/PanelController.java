package Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class PanelController implements Initializable {

    @FXML
    ComboBox<String> diskSelector = new ComboBox<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDiskSelector();
    }

    private void initDiskSelector(){
        diskSelector.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()){
            diskSelector.getItems().add(path.toString());
        }
        diskSelector.getSelectionModel().select(0);
    }
}
