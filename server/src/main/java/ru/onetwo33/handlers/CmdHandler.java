package ru.onetwo33.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import ru.onetwo33.model.Cmd;
import ru.onetwo33.model.FileInfo;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.READ;

public class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
        String name = msg.name().toString(StandardCharsets.UTF_8);
        String args = msg.args().toString(StandardCharsets.UTF_8);

        FileChannel fc = null;
        long length = -1;

        if ("ls".equals(name)) {
            if (!args.isEmpty()) {
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
                System.out.println(123);
            }
        } else if ("download".equals(name)) {
            if (!args.isEmpty()) {
                String pathString = args.replaceAll("\\\\", "/");
                Path path = Paths.get(pathString);
                uploadFile(ctx, fc, length, path);
            }
        } else if ("mkdir".equals(name)) {
            if (!args.isEmpty()) {

            }
        } else if ("delete".equals(name)) {
            if (!args.isEmpty()) {

            }
        }

        ReferenceCountUtil.release(msg);
    }

    private void uploadFile(ChannelHandlerContext ctx, FileChannel fc, long length, Path path) throws IOException {
        String filename = path.getFileName().toString();
        try {
            fc = FileChannel.open(path, READ);
            length = fc.size();
        } catch (Exception e) {
            ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n');
        } finally {
            if (length < 0 && fc != null) {
                fc.close();
            }
        }

        ByteBuf buf = ctx.alloc().directBuffer();
        buf.writeBytes(("OK: " + filename + " " + fc.size()).getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
        ctx.writeAndFlush(new DefaultFileRegion(fc, 0, length));
    }

    private List<FileInfo> updateList(Path path) throws IOException {
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());
    }
}
