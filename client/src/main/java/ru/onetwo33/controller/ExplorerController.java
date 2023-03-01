package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.util.UtilsExplorer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    public void upload(String dest) throws IOException {
        String command = "upload";

        if (UtilsExplorer.getSelectedFilename(filesTable) == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Path path = Paths.get(filesTable.getSelectionModel().getSelectedItem().getFilename());
        File file1 = Paths.get(pathField.getText())
                .resolve(filesTable
                        .getSelectionModel()
                        .getSelectedItem()
                        .getFilename()).toFile();
        String file = command + " " + dest + path.toString().replaceAll(" ", "_") + " " + file1.length() + "\r\n";
        ByteBuf buffer = channel.alloc().directBuffer();
        buffer.writeBytes(file.getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buffer);
//        channel.writeAndFlush(new DefaultFileRegion(file, 0, length));
        uploadFile(channel, file1);
    }

    private void uploadFile(Channel ctx, File file) throws IOException {
//        String filename = file.getName().replaceAll(" ", "_");
        long length = file.length();

        ByteBuf buf = ctx.alloc().directBuffer();
//        buf.writeBytes(("OK: " + filename + " " + length).getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
        ctx.writeAndFlush(new DefaultFileRegion(file, 0, length));
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
