package ru.onetwo33.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.onetwo33.model.NetworkConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private NetworkConnection connection;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = new NetworkConnection();
    }

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
