package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

@ChannelHandler.Sharable
public class JsonMessageEncoder extends MessageToMessageEncoder<Object> {
    private static Logger logger = LoggerFactory.getLogger(JsonMessageEncoder.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonMessageEncoder() {
        objectMapper = DTOClassMessageTypeMapper.prepareObjectMapper();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = Unpooled.buffer();
        ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf);
        ObjectWriter objectWriter = objectMapper
                .writerFor(msg.getClass())
                .withAttribute(
                        DTOClassMessageTypeMapper.MAPPING_FIELD,
                        DTOClassMessageTypeMapper.getMessageTypeByDTOClass(msg.getClass())
                );
        objectWriter.writeValue((OutputStream) byteBufOutputStream, msg);
        if (logger.isDebugEnabled()) {
            String writtenValue = encode(msg, objectWriter);
            logger.debug("Encoded: {}", writtenValue);
        }
        out.add(byteBuf);
    }
    
    private static String encode(Object obj, ObjectWriter objectWriter) throws JsonProcessingException {
        return objectWriter.writeValueAsString(obj);
    }
    
    public static String encode(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = DTOClassMessageTypeMapper.prepareObjectMapper();
        ObjectWriter objectWriter = objectMapper
                .writerWithDefaultPrettyPrinter()
                .withAttribute(
                        DTOClassMessageTypeMapper.MAPPING_FIELD,
                        DTOClassMessageTypeMapper.getMessageTypeByDTOClass(obj.getClass())
                );
        return encode(obj, objectWriter);
    }
}
