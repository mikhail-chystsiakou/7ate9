package com.yatty.sevennine.backend;

import com.yatty.sevennine.util.PropertiesProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Properties;

public class TCPServerStub {
    static SocketAddress address;
    
    public static void main(String[] args) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup group = new NioEventLoopGroup(5);
    
        Properties environmentProperties = PropertiesProvider.getEnvironmentProperties();
        InetSocketAddress connectionPort = new InetSocketAddress(
                Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
        );

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(connectionPort);

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ReadTimeoutHandler(30));
                    socketChannel.pipeline().addLast(new HelloServerHandler());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
    
    public static class HelloServerHandlerDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx,
                              ByteBuf msg, List<Object> out) throws Exception {
            try {
                String received = msg.toString(CharsetUtil.UTF_8);
                System.out.println("Server data: '" + received + "', thread: " + Thread.currentThread().getId());
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
        
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf inBuffer = (ByteBuf) msg;
            String received = inBuffer.toString(CharsetUtil.UTF_8);
            System.out.println("Server data: '" + received + "', thread: " + Thread.currentThread().getId());
        }
    
        @Override
        public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
            cause.printStackTrace();
            channelHandlerContext.close();
        }
    }
    
    public static class HelloServerHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf inBuffer = (ByteBuf) msg;
    
            String received = inBuffer.toString(CharsetUtil.UTF_8);
            ctx.writeAndFlush(Unpooled.copiedBuffer(received, CharsetUtil.UTF_8)).sync();
            System.out.println("Server data: '" + received + "', thread: " + Thread.currentThread().getId());
            address = ctx.channel().remoteAddress();
            ctx.channel().disconnect();
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                    
                    
                    ctx.channel().connect(address);
                    ctx.channel().writeAndFlush(received).addListener(e -> {
                        if (e.isSuccess()) {
                        
                        } else {
                            e.cause().printStackTrace();
                        }
                    }).sync();
                    System.out.println("Sent twice");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
//            for (int i = 0; i < 5; i++) {
//                Thread.sleep(1000);
//                ctx.channel().config().al
//            }
        }
        
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
