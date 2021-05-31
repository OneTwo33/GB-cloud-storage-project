package ru.onetwo33.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.model.NetworkConnection;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExplorerCloudController implements Initializable {
    private NetworkConnection connection;

    @FXML
    TableView cloudFilesTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (connection == null) {
            connection = new NetworkConnection();
        }
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        cloudFilesTable.getColumns().addAll(fileTypeColumn);

        updateList(Paths.get("."));
    }

    private void updateList(Path path) {
        try {
            cloudFilesTable.getItems().clear();
            cloudFilesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            cloudFilesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов");
            alert.showAndWait();
        }
    }
}
