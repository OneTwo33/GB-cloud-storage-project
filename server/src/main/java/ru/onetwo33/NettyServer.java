package ru.onetwo33;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import ru.onetwo33.handlers.CmdHandler;
import ru.onetwo33.handlers.FileUploadServerHandler;
import ru.onetwo33.handlers.MessageHandler;
import ru.onetwo33.codecs.CmdDecoder;

public class NettyServer {
    public static final int PORT = 4000;

    public NettyServer() {

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new CmdDecoder(64 * 1024),
                                    new CmdHandler()
//                                    new StringEncoder(),
//                                    new StringDecoder(), !
//                                    new ObjectEncoder(),
//                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                                    new MessageHandler()
//                                    new ObjectEncoder(),
//                                    new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)),
//                                    new FileUploadServerHandler()
//                                    new LineEncoder()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("Server started");
            future.channel().closeFuture().sync();
            System.out.println("Server closed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
