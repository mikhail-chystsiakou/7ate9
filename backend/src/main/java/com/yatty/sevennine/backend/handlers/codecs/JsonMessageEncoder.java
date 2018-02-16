package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.List;

public class JsonMessageEncoder extends MessageToMessageEncoder<Object> {
    private static Logger logger = LoggerFactory.getLogger(JsonMessageEncoder.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        logger.trace("Encoding...");
        ByteBuf byteBuf = Unpooled.buffer();
        ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf);
        objectMapper.writeValue((OutputStream) byteBufOutputStream, msg);
        out.add(byteBuf);
    }
}
