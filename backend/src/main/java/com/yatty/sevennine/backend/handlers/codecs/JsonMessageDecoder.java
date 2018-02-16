package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.backend.TestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final String TYPE_FIELD = "_type";
    private static final Map<String, Class<?>> classTypeMapping;
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        classTypeMapping = new HashMap<>();
        classTypeMapping.put(TestMessage.TYPE, TestMessage.class);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        System.out.println("Decoding...");
        String data = msg.toString(StandardCharsets.UTF_8);
        JsonNode node = new ObjectMapper().readTree(data);
        String type = node.get("_type").asText();
        System.out.println("Parsed type: " + type);
        Class<?> clazz = classTypeMapping.get(type);
        System.out.println("Parsed class: " + clazz);
        Object v = objectMapper.readValue(data, clazz);
        out.add(v);
    }
}