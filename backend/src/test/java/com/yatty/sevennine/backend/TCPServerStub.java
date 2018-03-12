package com.yatty.sevennine.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.DisconnectRequest;
import com.yatty.sevennine.api.dto.MoveRequest;
import com.yatty.sevennine.api.dto.TestRequest;
import com.yatty.sevennine.backend.handlers.*;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import com.yatty.sevennine.backend.testing.TestMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCPServerStub {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(5);

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("localhost", 39405));

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
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
