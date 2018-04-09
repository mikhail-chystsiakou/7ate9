package com.yatty.sevennine.backend;

import com.yatty.sevennine.util.codecs.JsonMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class MultipleClientsTest {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("127.0.0.1", 39405));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new JsonMessageDecoder());
                }
            });
            Channel channelFuture = clientBootstrap.connect().sync().channel();
            channelFuture.writeAndFlush(Unpooled.copiedBuffer("{\"name\":\"name\",\"_type\":\"ConnectRequest\"}", CharsetUtil.UTF_8));
            channelFuture.closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
    
    public static class ClientHandler extends SimpleChannelInboundHandler {
    
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Client received: " + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
        
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
            cause.printStackTrace();
            channelHandlerContext.close();
        }
    }
    
    public static class HelloServerHandlerDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx,
                              ByteBuf msg, List<Object> out) throws Exception {
            try {
                String received = msg.toString(CharsetUtil.UTF_8);
                System.out.println("Client data: '" + received + "', thread: " + Thread.currentThread().getId());
                msg.readBytes(msg.readableBytes());
            } finally {
//                ReferenceCountUtil.release(msg);
            }
        }
        
//        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            ByteBuf inBuffer = (ByteBuf) msg;
//            String received = inBuffer.toString(CharsetUtil.UTF_8);
//            System.out.println("Server data: '" + received + "', thread: " + Thread.currentThread().getId());
//        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
            cause.printStackTrace();
            channelHandlerContext.close();
        }
    }
}
