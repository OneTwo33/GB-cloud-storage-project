package ru.onetwo33.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.onetwo33.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MessageHandler extends SimpleChannelInboundHandler<String> {

    private Path currentPath = Path.of(".");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from GUI: " + msg);

        String[] cmds = msg.split(" ");

        if ("ls".equals(cmds[0])) {
//            System.out.println(getFileList());
            List<FileInfo> fileInfos = updateList(currentPath);
//            for (FileInfo fi : fileInfos) {
//                ctx.writeAndFlush(fi);
//            }
            ctx.writeAndFlush(fileInfos);
        }

        ctx.writeAndFlush(msg);
    }

    private String getFileList() {
        return String.join(" ", new File(currentPath.toString()).list());
    }

    private List<FileInfo> updateList(Path path) throws IOException {
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());
    }
}
