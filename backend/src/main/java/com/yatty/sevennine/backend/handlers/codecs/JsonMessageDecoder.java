package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.*;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.backend.exceptions.io.DecodingException;
import com.yatty.sevennine.backend.testing.TestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to decode inbound server messages aka requests.
 * Also connects channel to the sender.
 *
 * @author Mike
 * @version 17.02.17
 */
public class JsonMessageDecoder extends ByteToMessageDecoder {
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
            logger.debug("Decoded: {}", v);
            out.add(v);
        } catch (Exception e) {
            throw new DecodingException("Failed to decode '" + data + "'", e);
        }
    }
    
    private static Object decode(String data, ObjectMapper objectMapper) throws IOException {
        JsonNode node = new ObjectMapper().readTree(data);
        String type = node.get(DTOClassMessageTypeMapper.MAPPING_FIELD).asText();
        System.out.printf(type);
        Class<?> clazz = DTOClassMessageTypeMapper.getDTOClassByMessageType(type);
        objectMapper.readValue(data, clazz);
        return objectMapper.readValue(data, clazz);
    }
    
    public static Object decode(String data) throws IOException {
        return decode(data, new ObjectMapper());
    }
}