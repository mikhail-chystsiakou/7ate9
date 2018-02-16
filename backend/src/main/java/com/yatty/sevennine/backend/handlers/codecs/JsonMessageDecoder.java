package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.ConnectResponse;
import com.yatty.sevennine.backend.testing.TestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yatty.sevennine.backend.util.Constants.PEER_ADDRESS_KEY;

public class JsonMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(JsonMessageDecoder.class);
    private static final String TYPE_FIELD = "_type";
    private static final Map<String, Class<?>> classTypeMapping;
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        classTypeMapping = new HashMap<>();
        classTypeMapping.put(TestMessage.TYPE, TestMessage.class);
        classTypeMapping.put(ConnectRequest.TYPE, ConnectRequest.class);
        classTypeMapping.put(ConnectResponse.TYPE, ConnectResponse.class);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        logger.trace("Decoding...");
        ctx.channel().attr(PEER_ADDRESS_KEY).set(msg.sender());
        try {
            String data = msg.content().toString(StandardCharsets.UTF_8);
            JsonNode node = new ObjectMapper().readTree(data);
            String type = node.get("_type").asText();
            Class<?> clazz = classTypeMapping.get(type);
            Object v = objectMapper.readValue(data, clazz);
            logger.debug("Decoded: {}", v);
            out.add(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}