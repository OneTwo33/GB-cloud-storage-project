package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.util.UtilsExplorer;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ExplorerCloudController extends AuthController implements Initializable {

    @FXML
    public TableView<FileInfo> cloudFilesTable;
    @FXML
    public TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilsExplorer.buildExplorer(cloudFilesTable);

        ByteBuf buffer = channel.alloc().directBuffer();
        byte[] bytes = "ls .\r\n".getBytes(StandardCharsets.UTF_8);
        buffer.writeBytes(bytes);
        channel.writeAndFlush(buffer);

        cloudFilesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText())
                        .resolve(cloudFilesTable
                                .getSelectionModel()
                                .getSelectedItem()
                                .getFilename());
//                if (Files.isDirectory(path)) {
                    String dir = ("ls " + path + "\r\n");
                    ByteBuf buf = channel.alloc().directBuffer();
                    buf.writeBytes(dir.getBytes(StandardCharsets.UTF_8));
                    channel.writeAndFlush(buf);
//                }
            }
        });
    }

//    public String getSelectedFilename() {
//        if (!cloudFilesTable.isFocused()) {
//            return null;
//        }
//        return cloudFilesTable.getSelectionModel().getSelectedItem().getFilename();
//    }
//
//    public String getCurrentPath() {
//        return pathField.getText();
//    }

    @FXML
    public void getRoot(ActionEvent actionEvent) {
        ByteBuf buffer = channel.alloc().directBuffer();
        buffer.writeBytes("ls \\\r\n".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buffer);
    }

    public void download() {
        Path path = Paths.get(pathField.getText())
                .resolve(cloudFilesTable
                        .getSelectionModel()
                        .getSelectedItem()
                        .getFilename());
        String file = "download " + path + "\r\n";
        ByteBuf buffer = channel.alloc().directBuffer();
        buffer.writeBytes(file.getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buffer);
    }

    public void delete() {
    }

    public void updateList(Path path, List<FileInfo> fileInfoList) {
        try {
            pathField.setText(path.normalize().toString());
            cloudFilesTable.getItems().clear();
            cloudFilesTable.getItems().addAll(fileInfoList);
            cloudFilesTable.sort();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "???? ??????????-???? ?????????????? ???? ?????????????? ???????????????? ???????????? ????????????");
            alert.showAndWait();
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        ByteBuf buffer = channel.alloc().directBuffer();
        byte[] bytes = ("ls " + upperPath.toString() + "\r\n").getBytes(StandardCharsets.UTF_8);
        buffer.writeBytes(bytes);
        channel.writeAndFlush(buffer);
    }


}
