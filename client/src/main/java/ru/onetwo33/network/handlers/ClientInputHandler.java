package ru.onetwo33.network.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import ru.onetwo33.controller.ExplorerCloudController;
import ru.onetwo33.controller.ExplorerController;
import ru.onetwo33.model.FileInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ClientInputHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ExplorerController explorerController;
    private final ExplorerCloudController explorerCloudController;
    private Path path;
    private List<FileInfo> fileInfos;
    private String allDirs = "";

    public ClientInputHandler(ExplorerController explorerController, ExplorerCloudController explorerCloudController) {
        this.explorerController = explorerController;
        this.explorerCloudController = explorerCloudController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        byte[] bytes = new byte[buf.readableBytes()];

        buf.readBytes(bytes);
        String str = new String(bytes, StandardCharsets.UTF_8);

        if (str.contains("OK")) {
            String[] attrs = str.split(" ");
            long size = Long.parseLong(attrs[2]);
            ctx.channel().pipeline().addFirst(new FileInputHandler(explorerController, attrs[1], size));
//            ctx.channel().pipeline().addFirst(new FileInputHandler(explorerController));
            ctx.fireChannelRead(attrs);
            return;
        }

        if (str.contains("File send OK")) {
            ctx.channel().pipeline().removeFirst();
            return;
        }

        allDirs = allDirs + str;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(allDirs, Map.class);
        path = objectMapper.convertValue(map.get("path"), new TypeReference<Path>(){});
        fileInfos = objectMapper.convertValue(map.get("fileinfos"), new TypeReference<List<FileInfo>>(){});
        allDirs = "";

        Platform.runLater( () -> explorerCloudController.updateList(path, fileInfos) );
    }
}
