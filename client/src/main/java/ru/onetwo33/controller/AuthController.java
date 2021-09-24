package ru.onetwo33.controller;

import io.netty.channel.socket.SocketChannel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.onetwo33.network.NetworkConnection;

import java.io.IOException;

public class AuthController {

    private SocketChannel channel = NetworkConnection.getChannel();

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    public void successAuth(String authWindow) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(authWindow));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public void successAuth(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        btn.getParent().getScene().getWindow().hide();
        successAuth("/view/main.fxml");
    }
}
