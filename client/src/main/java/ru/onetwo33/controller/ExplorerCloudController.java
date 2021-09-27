package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.onetwo33.model.FileInfo;
import ru.onetwo33.network.NetworkConnection;
import ru.onetwo33.util.UtilsExplorer;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ExplorerCloudController implements Initializable {

    private final SocketChannel channel = NetworkConnection.getChannel();

    @FXML
    TableView<FileInfo> cloudFilesTable;
    @FXML
    TextField pathField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UtilsExplorer.buildExplorer(cloudFilesTable);
        UtilsExplorer.updateList(Paths.get("."), pathField, cloudFilesTable);
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

    public void copy() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Фокус на правой", ButtonType.OK);
        alert.showAndWait();
    }

    public void delete() {
    }
}
