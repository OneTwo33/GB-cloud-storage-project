package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.network.NetworkConnection;
import ru.onetwo33.util.UtilsExplorer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ExplorerController implements Initializable {

    private final SocketChannel channel = NetworkConnection.getChannel();

    @FXML
    TableView<FileInfo> filesTable;

    @FXML
    ComboBox<String> disksBox;

    @FXML
    TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilsExplorer.buildExplorer(filesTable);

        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        filesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText())
                        .resolve(filesTable
                                .getSelectionModel()
                                .getSelectedItem()
                                .getFilename());
                if (Files.isDirectory(path)) {
                    UtilsExplorer.updateList(path, pathField, filesTable);
                }
            }
        });

        UtilsExplorer.updateList(Paths.get("."), pathField, filesTable);
    }

    @FXML
    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            UtilsExplorer.updateList(upperPath, pathField, filesTable);
        }
    }

    @FXML
    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        UtilsExplorer.updateList(Paths.get(element.getSelectionModel().getSelectedItem()), pathField, filesTable);
    }

    public String getSelectedFilename() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }

    public void copy() {
        if (getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (getSelectedFilename() != null) {
            Path srcPath = Paths.get(getCurrentPath(), getSelectedFilename());
            Path dstPath = Paths.get(getCurrentPath()).resolve(getCurrentPath());

            try {
                //                String command = "upload ";
                ByteBuf buffer = channel.alloc().directBuffer();
                //                Files.readAllBytes(srcPath)
                //                buffer.writeBytes(command.getBytes(StandardCharsets.UTF_8));
                buffer.writeBytes(Files.readAllBytes(srcPath));
                channel.writeAndFlush(buffer);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопировать указанный файл", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    public void delete() {
    }
}
