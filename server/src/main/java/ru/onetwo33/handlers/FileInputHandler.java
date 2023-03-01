package ru.onetwo33.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.onetwo33.codecs.CmdDecoder;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

public class FileInputHandler extends SimpleChannelInboundHandler<ByteBuf> {

//    private final String name;
    private final long size;
    private Path file;

    public FileInputHandler(String name, long size) {
        this.file = Paths.get(name);
        this.size = size;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
//        String filename = file.getName().replaceAll(" ", "_");
//        file = Paths.get(name);
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
        if (Files.exists(file)) {
            System.out.println(size + " FROM TO: " + Files.size(file));
            if (Files.size(file) == size) {
//            UtilsExplorer.updateList(explorerController.filesTable,
//                    explorerController.pathField,
//                    Paths.get(explorerController.pathField.getText()));
                ctx.channel().pipeline().addFirst(new CmdDecoder(64 * 1024));
                ctx.channel().pipeline().remove(this);
            }
        }
    }
}
