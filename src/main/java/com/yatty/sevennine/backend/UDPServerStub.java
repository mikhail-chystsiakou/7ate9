package com.yatty.sevennine.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.backend.testing.ClientStub;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UDPServerStub {
    private static final Logger logger = LoggerFactory.getLogger(UDPServerStub.class);
    private static Map<InetSocketAddress, Channel> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Properties environmentProperties = PropertiesProvider.getEnvironmentProperties();
        new UDPServerStub().start(environmentProperties);
    }

    public void start(Properties environmentProperties) throws IOException, InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ServerChannelInitializer());
            String connectionPort = environmentProperties.getProperty(PropertiesProvider.Environment.PORT);
            System.out.println("Server started");
            b.bind(Integer.valueOf(connectionPort)).sync().channel().closeFuture().await();
            System.out.printf("Server started2");
        } finally {
            group.shutdownGracefully();
        }
    }

//    public static void stop() {
//        if (channel != null) {
//            channel.close();
//        }
//        workerGroup.shutdownGracefully();
//    }

    public static class ServerChannelInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
//            ch.pipeline().addLast(new ClientStub.BasicJacksonCodec<>(TestMessage.class, new ObjectMapper()));
//            ch.pipeline().addLast(new TestMessageDecoder());
            ch.pipeline().addLast(new DatagramPacketDecoder(new TestMessageDecoder()));
//            ch.pipeline().addLast(new InboundHandler());
            ch.pipeline().addLast(new ObjectInboundHandler());
//            ch.pipeline().addLast(new ByteArrayEncoder());
        }
    }

    public static class TestMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            System.out.println("Decoding...");
            ByteBufInputStream byteBufInputStream = new ByteBufInputStream(msg);
            out.add(new ObjectMapper().readValue((InputStream) byteBufInputStream, TestMessage.class));
            // copy the ByteBuf content to a byte array
//            byte[] array = new byte[msg.readableBytes()];
//            msg.getBytes(0, array);
//
//            out.add(array);
        }
    }

    public static class ObjectInboundHandler extends SimpleChannelInboundHandler<TestMessage> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TestMessage msg) throws Exception {
            System.out.println("Got message with data: " + msg.toString());
        }
    }

    public static class InboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println(cause.getMessage());
            ctx.close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            String data = msg.content().toString(StandardCharsets.UTF_8);
            logger.debug("Got data {}", data);
            switch (data) {
                case "connect" : {
                    clients.put(msg.sender(), ctx.channel());
                    System.out.println("Client added: " + msg.sender());
                    DatagramPacket dp  = new DatagramPacket(
                            Unpooled.copiedBuffer(msg.content().toString(), CharsetUtil.UTF_8),
                            msg.sender());
                    ctx.writeAndFlush(dp).sync();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            InetSocketAddress clientAddress = clients.keySet().iterator().next();
                            logger.debug("Sending response to {}", clientAddress);
                            DatagramPacket response  = new DatagramPacket(
                                    Unpooled.copiedBuffer(msg.content().toString(), CharsetUtil.UTF_8),
                                    msg.sender());
                            clients.get(clientAddress).writeAndFlush(response);
                        }
                    }, 3000L);
                    logger.debug("Message {} sent to the client {}", dp ,msg.sender());
                    ctx.writeAndFlush(Unpooled.wrappedBuffer("ok".getBytes()));
                    break;
                }
                default: {
                    System.out.println("Got " + data);
                }
            }
//            InetSocketAddress sender = msg.sender();
//            ctx.writeAndFlush(new DatagramPacket(
//                    Unpooled.copiedBuffer(msg.content().toString(), CharsetUtil.UTF_8),
//                    msg.sender()
//            )).sync();
        }
    }
}
