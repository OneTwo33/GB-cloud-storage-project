package ru.onetwo33.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufInputHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()) {
            sb.append((char) buf.readByte());
        }
//        File file = new File("../../test/resources/test_write_using_filechannel.txt");
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        try (RandomAccessFile writer = new RandomAccessFile(file, "rw");
//            FileChannel channel = writer.getChannel()){
//            while (buf.isReadable()) {
//                channel.write(ByteBuffer.allocateDirect(buf.readByte()));
//            }
//        }
        System.out.println(sb);
        ctx.writeAndFlush(sb.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
