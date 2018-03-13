package com.yatty.sevennine.backend;

import com.yatty.sevennine.backend.handlers.*;
import com.yatty.sevennine.backend.handlers.auth.LogInHandler;
import com.yatty.sevennine.backend.handlers.auth.LogOutHandler;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import com.yatty.sevennine.backend.handlers.lobby.LobbySubscribeHandler;
import com.yatty.sevennine.backend.handlers.lobby.LobbyUnsubscribeHandler;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class TCPServer {
    private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);
    
    public static void main(String[] args) {
        Properties environmentProperties = null;
        try {
            environmentProperties = PropertiesProvider.getEnvironmentProperties();
            new TCPServer().start(environmentProperties);
        } catch (IOException | InterruptedException e) {
            logger.error("Can not initialize server", e);
        }
    }
    
    public void start(Properties environmentProperties) throws IOException, InterruptedException {
        // TODO: proper bootstrap. Current version creates single channel for all connections
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer());
            InetSocketAddress connectionPort = new InetSocketAddress(
                    Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
            );
            Channel channel = b.bind(connectionPort).sync().channel();
            logger.debug("Server started...");
            channel.closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    public static class ServerChannelInitializer
            extends ChannelInitializer<SocketChannel> {
        
        private ChannelHandler connectHandler = new ConnectHandler();
        private ChannelHandler testHandler = new TestHandler();
        private ChannelHandler moveRequestHandler = new MoveRequestHandler();
        private ChannelHandler disconnectHandler = new DisconnectHandler();
        private ChannelHandler encoder = new JsonMessageEncoder();
        private ChannelHandler finalCleanupHandler = new FinalCleanupHandler();
        private ChannelHandler logInHandler = new LogInHandler();
        private ChannelHandler logOutHandler = new LogOutHandler();
        private ChannelHandler lobbySubscribeHandler = new LobbySubscribeHandler();
        private ChannelHandler lobbyUnsubscribeHandler = new LobbyUnsubscribeHandler();
    
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            logger.debug("Initializing socket channel...");
            ch.pipeline().addFirst("decodeHandler", new JsonMessageDecoder());
            
            ch.pipeline().addLast("loginHandler", logInHandler);
            ch.pipeline().addLast("logoutHandler", logOutHandler);
    
    
            ch.pipeline().addLast("loginHandler", lobbySubscribeHandler);
            ch.pipeline().addLast("logoutHandler", lobbyUnsubscribeHandler);
    
    
            ch.pipeline().addLast("connectHandler", connectHandler);
            ch.pipeline().addLast("testHandler", testHandler);
            ch.pipeline().addLast("moveRequestHandler", moveRequestHandler);
            ch.pipeline().addLast("disconnectHandler", disconnectHandler);
            
            ch.pipeline().addLast("encodeHandler", encoder);
            ch.pipeline().addLast("cleanupHandler", finalCleanupHandler);
        }
    }
}
