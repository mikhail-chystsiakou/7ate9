package com.yatty.sevennine.util.codecs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * Used to decode inbound server messages aka requests.
 * Also connects channel to the sender.
 *
 * @author Mike
 * @version 17.02.17
 */
public class JsonMessageDecoder extends MessageToMessageDecoder<ByteBuf> implements Function<String, Object> {
    private static final Logger logger = LoggerFactory.getLogger(JsonMessageDecoder.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonMessageDecoder() {
        objectMapper = new ObjectMapper();
        objectMapper.addMixIn(Object.class, DTOTypeMappingMixin.class);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf msg, List<Object> out) throws Exception {
        String data = msg.readBytes(msg.readableBytes()).toString(StandardCharsets.UTF_8);
        logger.debug("Decoding '{}'", data);
        try {
            Object v = decode(data, objectMapper);
            out.add(v);
        } catch (Exception e) {
            throw new DecoderException("Failed to decode '" + data + "'", e);
        }
    }
    
    @Override
    public Object apply(String data) {
        try {
            return decode(data, objectMapper);
        } catch (IOException e) {
            throw new IllegalArgumentException(data, e);
        }
    }
    
    protected static Object decode(String data, ObjectMapper objectMapper) throws IOException {
        JsonNode node = new ObjectMapper().readTree(data);
        String type = node.get(DTOClassMessageTypeMapper.MAPPING_FIELD).asText();
        Class<?> clazz = DTOClassMessageTypeMapper.getDTOClassByMessageType(type);
        objectMapper.readValue(data, clazz);
        return objectMapper.readValue(data, clazz);
    }
}