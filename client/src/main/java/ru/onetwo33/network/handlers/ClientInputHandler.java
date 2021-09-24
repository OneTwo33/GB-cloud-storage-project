package ru.onetwo33.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.onetwo33.model.FileInfo;

import java.util.List;

//public class ClientInputHandler extends ChannelInboundHandlerAdapter {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        List<FileInfo> list = (List<FileInfo>) msg;
////        FileInfo fi = (FileInfo) msg;
//        System.out.println(123);
//    }
//}

public class ClientInputHandler extends SimpleChannelInboundHandler<List<FileInfo>> {

    private final List<FileInfo> explorerList;

    public ClientInputHandler(List<FileInfo> explorerList) {
        this.explorerList = explorerList;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<FileInfo> msg) throws Exception {
        System.out.println(123);
    }
}
