package ru.onetwo33.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ru.onetwo33.network.NetworkConnection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    private SocketChannel channel = NetworkConnection.getChannel();
//    private EventLoopGroup group;

    @FXML
    private ExplorerController explorerController;
    @FXML
    private ExplorerCloudController explorerCloudController;
    @FXML
    VBox leftPanel, rightPanel;

    @FXML
    public void btnExitAction(ActionEvent actionEvent) {
        channel.close();
        Platform.exit();
    }

    @FXML
    public void copyBtnAction(ActionEvent actionEvent) {
        ExplorerController leftEC = (ExplorerController) leftPanel.getProperties().get("ctrl");
        ExplorerCloudController rightECC = (ExplorerCloudController) rightPanel.getProperties().get("ctrl");
//        for (Node node : leftPanel.getChildren()) {
        if (leftEC.filesTable.isFocused()) {
//                ExplorerController leftEC = (ExplorerController) leftPanel.getProperties().get("ctrl");
//                ExplorerCloudController rightECC = (ExplorerCloudController) rightPanel.getProperties().get("ctrl");

                if (leftEC.getSelectedFilename() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                if (leftEC.getSelectedFilename() != null) {
                    Path srcPath = Paths.get(leftEC.getCurrentPath(), leftEC.getSelectedFilename());
                    Path dstPath = Paths.get(rightECC.getCurrentPath()).resolve(rightECC.getCurrentPath());

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
//            }
        } else if (rightECC.cloudFilesTable.isFocused()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Фокус на правой", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не выбран файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean isChildFocused(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                return true;
            } else if (node instanceof Parent) {
                if (isChildFocused((Parent) node)) {
                    return true;
                }
            }
        }
        return false;
    }
}
