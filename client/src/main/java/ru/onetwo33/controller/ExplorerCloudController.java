package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.network.NetworkConnection;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExplorerCloudController implements Initializable {

//    private SocketChannel channel = NetworkConnection.getChannel();
    private Channel channel;
    private EventLoopGroup group;

    @FXML
    TableView<FileInfo> cloudFilesTable;
    @FXML
    TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        cloudFilesTable.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn);
        cloudFilesTable.getSortOrder().add(fileTypeColumn);

        updateList(Paths.get("."));
    }

    public String getSelectedFilename() {
        if (!cloudFilesTable.isFocused()) {
            return null;
        }
        return cloudFilesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }

    @FXML
    public void testSubmit(ActionEvent actionEvent) {
        System.out.println("Отправляем данные");
        ByteBuf buffer = channel.alloc().directBuffer();
        buffer.writeBytes("ls".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buffer);
    }

    private void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            cloudFilesTable.getItems().clear();
            cloudFilesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            cloudFilesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов");
            alert.showAndWait();
        }
    }
}
