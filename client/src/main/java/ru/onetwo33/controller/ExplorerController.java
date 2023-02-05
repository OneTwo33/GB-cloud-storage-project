package ru.onetwo33.controller;

import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.model.FileUploadFile;
import ru.onetwo33.network.handlers.FileUploadClientHandler;
import ru.onetwo33.util.UtilsExplorer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExplorerController extends AuthController implements Initializable {

//    private final SocketChannel channel = NetworkConnection.getChannel();

    @FXML
    public TableView<FileInfo> filesTable;

    @FXML
    public ComboBox<String> disksBox;

    @FXML
    public TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilsExplorer.buildExplorer(filesTable);

        updateList(Paths.get("."));

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
                    updateList(path);
                }
            }
        });
    }

    @FXML
    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    @FXML
    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
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

    public void upload() {
        if (getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (getSelectedFilename() != null) {
//            Path srcPath = Paths.get(getCurrentPath(), getSelectedFilename());
//            Path dstPath = Paths.get(getCurrentPath()).resolve(getCurrentPath());

            //                String command = "upload ";
//                ByteBuf buffer = channel.alloc().directBuffer();
//                //                Files.readAllBytes(srcPath)
//                //                buffer.writeBytes(command.getBytes(StandardCharsets.UTF_8));
//                buffer.writeBytes("copy ".getBytes(StandardCharsets.UTF_8));
//                buffer.writeBytes(Files.readAllBytes(srcPath));
//                channel.writeAndFlush(buffer);
            FileUploadFile uploadFile = new FileUploadFile();
            File file = new File("c:/Users/Admin/Desktop/process.txt");
            String fileMd5 = file.getName();//  file name
            uploadFile.setFile(file);
            uploadFile.setFile_md5(fileMd5);
            uploadFile.setStartPos(0);//  File start location
            channel.pipeline().addLast(new ObjectEncoder());
            channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
            channel.pipeline().addLast(new FileUploadClientHandler(uploadFile));
            channel.pipeline().remove(ObjectEncoder.class);
            channel.pipeline().remove(ObjectDecoder.class);
            channel.pipeline().remove(FileUploadClientHandler.class);
        }
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

        updateList(Paths.get(pathField.getText()));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов");
            alert.showAndWait();
        }
    }
}
