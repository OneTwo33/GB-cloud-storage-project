package ru.onetwo33.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import ru.onetwo33.model.Cmd;

public class CmdDecoder extends LineBasedFrameDecoder {
    private static final byte SPACE = (byte)' ';

    public CmdDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
        if (frame == null) {
            return null;
        }
        int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), SPACE);
        return new Cmd(frame.slice(frame.readerIndex(), index),
                frame.slice(index + 1, frame.writerIndex()  - (index + 1)));
    }
}
