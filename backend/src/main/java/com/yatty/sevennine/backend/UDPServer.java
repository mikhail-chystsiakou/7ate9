package com.yatty.sevennine.backend;

import com.yatty.sevennine.backend.handlers.ConnectHandler;
import com.yatty.sevennine.backend.handlers.DisconnectHandler;
import com.yatty.sevennine.backend.handlers.FinalCleanupHandler;
import com.yatty.sevennine.backend.handlers.MoveRequestHandler;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    public static void main(String[] args) {
        Properties environmentProperties = null;
        try {
            environmentProperties = PropertiesProvider.getEnvironmentProperties();
            new UDPServer().start(environmentProperties);
        } catch (IOException | InterruptedException e) {
            logger.error("Can not initialize server", e);
        }
    }

    public void start(Properties environmentProperties) throws IOException, InterruptedException {
//        EventLoopGroup group = new NioEventLoopGroup(5);
        // TODO: proper bootstrap. Current version creates single channel for all connections
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
//                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .channel(NioDatagramChannel.class)
                    .handler(new ServerChannelInitializer());
            InetSocketAddress connectionPort = new InetSocketAddress(
                    Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
            );
            logger.debug("Server started...");
            b.bind(connectionPort).sync().channel().closeFuture().await();
//            ChannelFuture future;
//            for(int i = 0; i < 5; ++i) {
//                future = b.bind(connectionPort).await();
//                if (!future.isSuccess()) {
//                    logger.error("Fail to bind to {}", connectionPort);
//                }
//            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static class ServerChannelInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ch.pipeline().addFirst("decodeHandler", new JsonMessageDecoder());
            ch.pipeline().addLast("connectHandler", new ConnectHandler());
            ch.pipeline().addLast("moveRequestHandler", new MoveRequestHandler());
            ch.pipeline().addLast("disconnectHandler", new DisconnectHandler());
            ch.pipeline().addLast("encodeHandler", new JsonMessageEncoder());
            ch.pipeline().addLast("cleanupHandler", new FinalCleanupHandler());
        }
    }
}
