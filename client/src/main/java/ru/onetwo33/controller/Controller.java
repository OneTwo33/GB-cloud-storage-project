package ru.onetwo33.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Controller {

    @FXML
    public ExplorerController explorerController;
    @FXML
    public ExplorerCloudController explorerCloudController;

    @FXML
    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void copyBtnAction(ActionEvent actionEvent) {
        if (explorerController.filesTable.isFocused()) {
            explorerController.upload();
        } else if (explorerCloudController.cloudFilesTable.isFocused()) {
            explorerCloudController.download();
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
