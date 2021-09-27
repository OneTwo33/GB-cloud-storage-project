package ru.onetwo33.controller;

import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.onetwo33.network.NetworkConnection;

public class Controller {

    private final SocketChannel channel = NetworkConnection.getChannel();

    @FXML
    private ExplorerController explorerController;
    @FXML
    private ExplorerCloudController explorerCloudController;

    @FXML
    public void btnExitAction(ActionEvent actionEvent) {
        channel.close();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void copyBtnAction(ActionEvent actionEvent) {
        if (explorerController.filesTable.isFocused()) {
            explorerController.copy();
        } else if (explorerCloudController.cloudFilesTable.isFocused()) {
            explorerCloudController.copy();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не выбран файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void deleteBtnAction(ActionEvent actionEvent) {
        if (explorerController.filesTable.isFocused()) {
            explorerController.delete();
        } else if (explorerCloudController.cloudFilesTable.isFocused()) {
            explorerCloudController.delete();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не выбран файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void mkdirBtnAction(ActionEvent actionEvent) {
        if (explorerController.filesTable.isFocused()) {

        } else if (explorerCloudController.cloudFilesTable.isFocused()) {

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите окно для создания папки", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
