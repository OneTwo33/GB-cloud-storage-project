package ru.onetwo33.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.onetwo33.network.handlers.ByteBufInputHandler;
import ru.onetwo33.network.handlers.ClientInputHandler;
import ru.onetwo33.network.handlers.ClientStringHandler;

public final class NetworkConnection {
    private static SocketChannel channel;

    private static final String HOST = "localhost";
    private static final int PORT = 4000;

    public NetworkConnection() {
    }

    public static SocketChannel getChannel() {
        if (channel == null) {
            new Thread(() -> {
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    channel = socketChannel;
                                    socketChannel.pipeline().addLast(
                                            new ObjectEncoder(),
                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                                            new ClientInputHandler(),
                                            new ClientStringHandler()
//                                            new ByteBufInputHandler(),
//                                            new OutputHandler()
//                                            new StringDecoder(),
//                                            new StringEncoder(),
//                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                                            new ObjectEncoder()
                                    );
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
        }

        return channel;
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }
}
