package ru.onetwo33.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import ru.onetwo33.codecs.CmdDecoder;
import ru.onetwo33.model.Cmd;
import ru.onetwo33.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CmdHandler extends SimpleChannelInboundHandler<Cmd> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
        String name = msg.name().toString(StandardCharsets.UTF_8);
        String args = msg.args().toString(StandardCharsets.UTF_8);

        if ("ls".equals(name)) {
            if (!args.isEmpty()) {
                System.out.println("Пришло " + name);
                ObjectMapper objectMapper = new ObjectMapper();
                ByteBuf buf = ctx.alloc().directBuffer();
                Map<String, Object> map = new HashMap<>();

                String correct = args.replaceAll("\\\\", "/");

                Path pathFromArgs = Paths.get(correct).normalize().toAbsolutePath();

                List<FileInfo> fileInfos = updateList(pathFromArgs);
                map.put("path", pathFromArgs);
                map.put("fileinfos", fileInfos);
                buf.writeBytes(objectMapper.writeValueAsBytes(map));

                ctx.writeAndFlush(buf);
            }
        } else if ("upload".equals(name)) {
            if (!args.isEmpty()) {
                String[] attrs = args.split(" ");
                long size = Long.parseLong(attrs[1]);
                ctx.channel().pipeline().addFirst(new FileInputHandler(attrs[0], size));
                ctx.channel().pipeline().remove(CmdDecoder.class);
//            ctx.channel().pipeline().addFirst(new FileInputHandler(explorerController));
//                ctx.fireChannelRead(attrs);
                return;
//                Files.createFile();
            }
        } else if ("download".equals(name)) {
            if (!args.isEmpty()) {
                String pathString = args.replaceAll("\\\\", "/");
                File file = Paths.get(pathString).toFile();
                uploadFile(ctx, file);
            }
        } else if ("mkdir".equals(name)) {
            if (!args.isEmpty()) {

            }
        } else if ("delete".equals(name)) {
            if (!args.isEmpty()) {
                String pathString = args.replaceAll("\\\\", "/");
                Path path = Paths.get(pathString);
                System.out.println("Args: " + args);
                System.out.println("pathString: " + pathString);
                System.out.println("Path: " + path);
                Files.delete(path);
            }
        }
        ReferenceCountUtil.release(msg);
    }

    private void uploadFile(ChannelHandlerContext ctx, File file) {
        String filename = file.getName().replaceAll(" ", "_");
        long length = file.length();

        ByteBuf buf = ctx.alloc().directBuffer();
        buf.writeBytes(("OK: " + filename + " " + length).getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
        ctx.writeAndFlush(new DefaultFileRegion(file, 0, length));
    }

    private List<FileInfo> updateList(Path path) throws IOException {
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());
    }
}
