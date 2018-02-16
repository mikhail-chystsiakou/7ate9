package com.yatty.sevennine.backend.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.backend.TestMessage;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.net.InetSocketAddress;

public class NettyUdpClient {
    private static volatile long startTime;
    public static final Object sem = new Object();

    public static void main(String[] args) throws Exception {
        new NettyUdpClient().start();
    }
    public void start() throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .remoteAddress(new InetSocketAddress(Constants.HOST, Constants.PORT))
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    public void initChannel(DatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new JsonMessageDecoder());
                        ch.pipeline().addLast(new DatagramPacketEncoder<>(new JsonMessageEncoder()));
//                        ch.pipeline().addLast(new ByteArrayEncoder());
//                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new EchoHandler());
                    }
                });

        System.out.println("Client started");
        startTime = System.nanoTime();
        Channel c = b.connect().sync().channel();
        c.writeAndFlush(new TestMessage("hello")).sync();
//        for(int i = 0; i < Constants.MESSAGES_COUNT; i++) {
//            c.writeAndFlush("test".getBytes()).sync();
//        }

        synchronized (sem) {
            sem.wait();
        }
        workerGroup.shutdownGracefully();
    }

    public static class EchoHandler extends SimpleChannelInboundHandler<Object> {
        private static volatile int messagesCount = 1;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Got message "+msg.toString());
            if (messagesCount++ >= Constants.MESSAGES_COUNT) {
                System.out.println("Client shutdown, time: " + (System.nanoTime() - NettyUdpClient.startTime));
                synchronized (sem) {
                    sem.notify();
                }
            }
        }
    }

//    public static class EchoHandler extends SimpleChannelInboundHandler<DatagramPacket> {
//        private static volatile int messagesCount = 1;
//
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
//            System.out.println("Got message from "+msg.sender());
//            if (messagesCount++ >= Constants.MESSAGES_COUNT) {
//                System.out.println("Client shutdown, time: " + (System.nanoTime() - NettyUdpClient.startTime));
//                synchronized (sem) {
//                    sem.notify();
//                }
//            }
//        }
//    }
}