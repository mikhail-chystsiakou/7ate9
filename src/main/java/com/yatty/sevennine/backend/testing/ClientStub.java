package com.yatty.sevennine.backend.testing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.backend.TestMessage;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class ClientStub {
    public static void main(String[] args) throws Exception {
        new ClientStub().run();
    }

    private void run() throws Exception {
        Properties p = PropertiesProvider.getEnvironmentProperties();
        EventLoopGroup elg = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(elg).channel(NioDatagramChannel.class).handler(new PipeLineInitializer());
        SocketAddress sa = new InetSocketAddress(p.getProperty(PropertiesProvider.Environment.HOST), Integer.valueOf(p.getProperty(PropertiesProvider.Environment.PORT)));
        Channel c = b.connect(sa).channel();
        ObjectMapper om = new ObjectMapper();
        String message = om.writeValueAsString(new TestMessage("Hello"));
        System.out.println("Sending " + message);
        c.writeAndFlush(new TestMessage("Hello")).addListener((e) -> {
            if (e.isSuccess()) {
                System.out.println("Message sent");
            } else {
                e.cause().printStackTrace();
            }
        });
    }

    private static class PipeLineInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ch.pipeline().addFirst(new BasicJacksonCodec<TestMessage>(TestMessage.class, new ObjectMapper()));
            ch.pipeline().addLast(new LogicHandler());
        }
    }

    private static class LogicHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("Got data: " + msg.toString(StandardCharsets.UTF_8));
        }
    }

    public static class BasicJacksonCodec<T> extends ByteToMessageCodec<T> {
        private final Class<T> clazz;
        private final ObjectMapper objectMapper;

        public BasicJacksonCodec(Class<T> clazz, ObjectMapper objectMapper) {
            super(clazz);
            this.clazz = clazz;
            this.objectMapper = objectMapper;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
            System.out.println("Encoding...");
            ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(out);
            objectMapper.writeValue((OutputStream) byteBufOutputStream, msg);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println("Decoding...");
            ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
            out.add(objectMapper.readValue((InputStream) byteBufInputStream, clazz));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

}
