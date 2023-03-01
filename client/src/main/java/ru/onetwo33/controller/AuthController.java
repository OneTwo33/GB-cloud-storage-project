package ru.onetwo33.controller;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.onetwo33.network.handlers.ClientInputHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController {

    protected Channel channel;
    private EventLoopGroup group;
    private Controller controller;
//    private static final String HOST = "3.87.207.72";
    private static final String HOST = "localhost";
    private static final int PORT = 4000;
    private BooleanProperty connected = new SimpleBooleanProperty(false);

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private HBox hboxStatus;

    @FXML
    private ProgressIndicator piStatus;

    @FXML
    private Label lblStatus;

    @FXML
    public void connect(ActionEvent actionEvent) {
        if( connected.get() ) {
            return;  // соединение уже установлено; предотвратить и отключить
        }

        group = new NioEventLoopGroup();
        Task<Channel> task = new Task<Channel>() {
            @Override
            protected Channel call() throws Exception {
                updateMessage("Bootstrapping");
                updateProgress(0.1d, 1.0d);

//                EventLoopGroup workerGroup = new NioEventLoopGroup();
                Bootstrap b = new Bootstrap();
                b
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .handler( new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                channel = ch;
                            }
                        });

                ChannelFuture f = b.connect(HOST, PORT);
                Channel chn = f.channel();

                updateMessage("Connecting");
                updateProgress(0.2d, 1.0d);

                f.sync();

                return chn;
            }

            @Override
            protected void succeeded() {
                channel = getValue();
                connected.set(true);

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/main.fxml"));

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Explorer");
                controller = loader.getController();
                channel.pipeline().addLast(new ClientInputHandler(controller.explorerController, controller.explorerCloudController));
//                .addLast(new FileInputHandler(controller.explorerController));
                stage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });

                stage.show();
            }

            @Override
            protected void failed() {
                Throwable exc = getException();
//                logger.error( "client connect error", exc );
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Client");
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();

                connected.set(false);
            }
        };


        hboxStatus.visibleProperty().bind( task.runningProperty() );
        lblStatus.textProperty().bind( task.messageProperty() );
        piStatus.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
////        if (channel == null) {
//        new Thread(() -> {
//            EventLoopGroup workerGroup = new NioEventLoopGroup();
//            try {
//                Bootstrap b = new Bootstrap();
//                b
//                        .group(workerGroup)
//                        .channel(NioSocketChannel.class)
//                        .handler(new ChannelInitializer<SocketChannel>() {
//                            @Override
//                            protected void initChannel(SocketChannel socketChannel) throws Exception {
//                                channel = socketChannel;
//                            }
//                        });
//                ChannelFuture future = b.connect(HOST, PORT).sync();
//                future.channel().closeFuture().sync();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                workerGroup.shutdownGracefully();
//            }
//        }).start();
////        }
//    }

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
        stage.setTitle("Explorer");
        controller = loader.getController();
        channel.pipeline().addLast(new ClientInputHandler(controller.explorerController, controller.explorerCloudController));
//                .addLast(new FileInputHandler(controller.explorerController));
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();


    }

    public void successAuth(ActionEvent actionEvent) {
        // TODO
        // Проверяю в базе пользователя и пароль
        // Если всё ок, то устанавливаю netty соединение
        // И передаю управление на след экран
        Button btn = (Button) actionEvent.getSource();
        btn.getParent().getScene().getWindow().hide();
        successAuth("/view/main.fxml");
    }
}
