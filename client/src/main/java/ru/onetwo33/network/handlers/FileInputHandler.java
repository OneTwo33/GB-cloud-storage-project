package ru.onetwo33.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.onetwo33.controller.ExplorerController;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileInputHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ExplorerController explorerController;
    private final String name;
    private final long size;
    private Path file;

    public FileInputHandler(ExplorerController explorerController, String name, long size) {
        this.explorerController = explorerController;
        this.name = name;
        this.size = size;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        file = Paths.get(explorerController.pathField.getText() + "\\" + name);
        if (Files.exists(file)) {
            try (OutputStream fout = new BufferedOutputStream(
                    Files.newOutputStream(file, APPEND)
            )) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                fout.write(bytes);
            }
        } else {
            try (OutputStream fout = new BufferedOutputStream(
                    Files.newOutputStream(file)
            )) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                fout.write(bytes);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (Files.size(file) >= size) {
            explorerController.updateList(Paths.get(explorerController.pathField.getText()));
            ctx.channel().pipeline().remove(this);
        }
    }
}
