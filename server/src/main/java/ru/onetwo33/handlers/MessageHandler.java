package ru.onetwo33.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import ru.onetwo33.model.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from GUI: " + msg);

        String[] cmds = msg.split(" ");

        if ("ls".equals(cmds[0])) {
            if (cmds[1] != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ByteBuf buf = ctx.alloc().directBuffer();
                Map<String, Object> map = new HashMap<>();
                Path path = Paths.get(cmds[1]);
                List<FileInfo> fileInfos = updateList(Paths.get(cmds[1]));
                map.put("path", path.normalize().toAbsolutePath());
                map.put("fileinfos", fileInfos);
                buf.writeBytes(objectMapper.writeValueAsBytes(map));

                ctx.writeAndFlush(buf);
            }
        } else if ("copy".equals(cmds[0])) {
            if (cmds[1] != null) {
                System.out.println(123);
            }
        } else if ("mkdir".equals(cmds[0])) {

        } else if ("delete".equals(cmds[0])) {

        }
//        ctx.writeAndFlush(msg);
        ReferenceCountUtil.release(msg);
    }

    private List<FileInfo> updateList(Path path) throws IOException {
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());
    }
}
