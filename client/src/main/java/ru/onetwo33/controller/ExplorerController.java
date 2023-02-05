package ru.onetwo33.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.util.UtilsExplorer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ExplorerController extends AuthController implements Initializable {

    @FXML
    public TableView<FileInfo> filesTable;

    @FXML
    public ComboBox<String> disksBox;

    @FXML
    public TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilsExplorer.buildExplorer(filesTable);

        UtilsExplorer.updateList(filesTable, pathField, Paths.get("."));

        // show current disk into disksBox
        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(Paths.get(pathField.getText()).getRoot().toString());

        filesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText())
                        .resolve(filesTable
                                .getSelectionModel()
                                .getSelectedItem()
                                .getFilename());
                if (Files.isDirectory(path)) {
                    UtilsExplorer.updateList(filesTable, pathField, path);
                }
            }
        });
    }

    @FXML
    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            UtilsExplorer.updateList(filesTable, pathField, upperPath);
        }
    }

    @FXML
    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        UtilsExplorer.updateList(filesTable, pathField, Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public void upload() {
        String command = "upload";
        UtilsExplorer.copy(channel, filesTable, pathField, command);
    }

    public void delete() {
        Path path = Paths.get(pathField.getText())
                .resolve(filesTable
                        .getSelectionModel()
                        .getSelectedItem()
                        .getFilename());
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UtilsExplorer.updateList(filesTable, pathField, Paths.get(pathField.getText()));
    }
}
