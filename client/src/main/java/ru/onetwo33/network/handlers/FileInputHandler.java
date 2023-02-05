package ru.onetwo33.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.onetwo33.controller.ExplorerController;
import ru.onetwo33.util.UtilsExplorer;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

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
        if (Files.notExists(file)) {
            file = Files.createFile(file);
        }

        try (OutputStream fout = new BufferedOutputStream(
                Files.newOutputStream(file, CREATE, APPEND, SYNC)
        )) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            fout.write(bytes);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (file != null && Files.size(file) == size) {
            UtilsExplorer.updateList(explorerController.filesTable,
                    explorerController.pathField,
                    Paths.get(explorerController.pathField.getText()));
            ctx.channel().pipeline().remove(this);
        }
    }
}
