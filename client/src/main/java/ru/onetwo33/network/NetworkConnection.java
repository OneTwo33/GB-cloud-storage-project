//package ru.onetwo33.network;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import ru.onetwo33.controller.ExplorerCloudController;
//import ru.onetwo33.model.FileInfo;
//import ru.onetwo33.network.handlers.ClientInputHandler;
//import ru.onetwo33.network.handlers.ClientStringHandler;
//
//import java.util.List;
//
//public final class NetworkConnection {
//    private static SocketChannel channel;
////    public static ObjectProperty<List<FileInfo>> receiveFileInfoListModel = new SimpleObjectProperty<List<FileInfo>>();
//
//    private static final String HOST = "localhost";
//    private static final int PORT = 4000;
//
//    public NetworkConnection() {
//    }
//
//    public static SocketChannel getChannel() {
//        if (channel == null) {
//            new Thread(() -> {
//                EventLoopGroup workerGroup = new NioEventLoopGroup();
//                try {
//                    Bootstrap b = new Bootstrap();
//                    b
//                        .group(workerGroup)
//                        .channel(NioSocketChannel.class)
//                        .handler(new ChannelInitializer<SocketChannel>() {
//                            @Override
//                            protected void initChannel(SocketChannel socketChannel) throws Exception {
//                                channel = socketChannel;
//                                socketChannel.pipeline().addLast(
//                                        new ObjectEncoder(),
//                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                                        new ClientInputHandler()
////                                        new ClientStringHandler()
////                                            new ByteBufInputHandler(),
////                                            new OutputHandler()
////                                            new StringDecoder(),
////                                            new StringEncoder(),
////                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
////                                            new ObjectEncoder()
//                                );
//                            }
//                        });
//                    ChannelFuture future = b.connect(HOST, PORT).sync();
//                    future.channel().closeFuture().sync();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    workerGroup.shutdownGracefully();
//                }
//            }).start();
//        }
//
//        return channel;
//    }
//}
