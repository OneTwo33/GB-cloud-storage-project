package ru.onetwo33.controller;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.onetwo33.network.handlers.ClientInputHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    protected static SocketChannel channel;
    private Controller controller;
//    private static final String HOST = "3.87.207.72";
    private static final String HOST = "localhost";
    private static final int PORT = 4000;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        if (channel == null) {
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b
                        .group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
//        }
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
