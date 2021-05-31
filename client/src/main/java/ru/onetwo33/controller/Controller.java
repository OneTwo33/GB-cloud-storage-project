package ru.onetwo33.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    VBox leftPanel, rightPanel;

    @FXML
    HBox content;

//    @FXML
//    private void changeButtons(ActionEvent event) throws IOException {
//        ExplorerController leftWindow = (ExplorerController) leftPanel.getProperties().get("crtl");
//        leftWindow.leftPanel.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (leftPanel.isFocused()) {
//                    try {
//                        content.getChildrenUnmodifiable().clear();
//                        System.out.println(123);
//                        content.getChildren().add(FXMLLoader.load(getClass().getResource("/view/explorerButtons.fxml")));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        if (rightPanel.isFocused()) {
//            content.getChildren().clear();
//            content.getChildren().add(FXMLLoader.load(getClass().getResource("/view/explorerCloudButtons.fxml")));
//        } else {
//            content.getChildren().add(FXMLLoader.load(getClass().getResource("/view/explorerButtons.fxml")));
//        }
////        content.getChildrenUnmodifiable().clear();
////        content.getChildrenUnmodifiable().add(FXMLLoader.load(getClass().getResource("/view/explorerCloudButtons.fxml")));
//    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {
        ExplorerController leftEC = (ExplorerController) leftPanel.getProperties().get("ctrl");
        ExplorerController rightEC = (ExplorerController) rightPanel.getProperties().get("ctrl");

        if (leftEC.getSelectedFilename() == null && rightEC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }


    }
}
