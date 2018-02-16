package com.yatty.sevennine.backend.testing;

import com.yatty.sevennine.backend.TestMessage;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.CharsetUtil;

public class NettyUdpServer {

    private int port;
    private static Channel channel;
    private static EventLoopGroup workerGroup;
    private volatile long elapsedTime = 0;

    public static void main(String[] args) throws Exception {
        new NettyUdpServer(Constants.PORT).start();
    }

    public NettyUdpServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ServerChannelInitializer());

            b.bind(Constants.PORT).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }

//
//        workerGroup = new NioEventLoopGroup();
//
//        ServerBootstrap bootstrap = new ServerBootstrap();
//        bootstrap.group(workerGroup)
//                .channel(NioServerSocketChannel.class)
//                .childHandler(new ServerChannelInitializer());
//
//        System.out.printf("Server started");
//        bootstrap.bind(new InetSocketAddress(Constants.PORT)).addListener((e) -> {
//            if (e.isSuccess()) {
//                System.out.println("Ok");
//            } else {
//                e.cause().printStackTrace();
//            }
//        });
    }

    public static void stop() {
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
    }

    public static class ServerChannelInitializer extends ChannelInitializer<DatagramChannel> {
        @Override
        protected void initChannel(DatagramChannel ch) throws Exception {
//            ch.pipeline().addLast(new ByteArrayDecoder());
//            ch.pipeline().addLast(new ByteArrayEncoder());
            ch.pipeline().addLast(new JsonMessageDecoder());
            ch.pipeline().addLast(new JsonMessageEncoder());
            ch.pipeline().addLast(new EchoHandler());
        }
    }
//    public static class EchoHandler extends SimpleChannelInboundHandler<DatagramPacket> {
//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//            ctx.flush();
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            System.err.println(cause.getMessage());
//            ctx.close();
//        }
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
//
//            ctx.channel().writeAndFlush(new DatagramPacket(
//                    Unpooled.copiedBuffer(msg.content().toString(), CharsetUtil.UTF_8),
//                    msg.sender()
//            )).addListener((e) -> {
//                if(!e.isSuccess()) e.cause().printStackTrace();
//            });
//        }
//    }

    public static class EchoHandler extends SimpleChannelInboundHandler<Object> {
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
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Got message");
            ctx.channel().writeAndFlush(new TestMessage("bye")).addListener((e) -> {
                if(!e.isSuccess()) e.cause().printStackTrace();
            });
        }
    }
}