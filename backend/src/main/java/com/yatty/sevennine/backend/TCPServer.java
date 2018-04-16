package com.yatty.sevennine.backend;

import com.yatty.sevennine.backend.handlers.*;
import com.yatty.sevennine.backend.handlers.auth.LogInHandler;
import com.yatty.sevennine.backend.handlers.auth.LogOutHandler;
import com.yatty.sevennine.backend.handlers.game.LeaveGameRequestHandler;
import com.yatty.sevennine.backend.handlers.game.MoveRequestHandler;
import com.yatty.sevennine.backend.handlers.lobby.CreateLobbyHandler;
import com.yatty.sevennine.backend.handlers.lobby.EnterLobbyHandler;
import com.yatty.sevennine.backend.handlers.lobby.LobbySubscribeHandler;
import com.yatty.sevennine.backend.handlers.lobby.LobbyUnsubscribeHandler;
import com.yatty.sevennine.util.PropertiesProvider;
import com.yatty.sevennine.util.codecs.JsonMessageDecoder;
import com.yatty.sevennine.util.codecs.JsonMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
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
        EventLoopGroup connectionEventGroup = new NioEventLoopGroup();
        EventLoopGroup dataEventGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(connectionEventGroup, dataEventGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer());
            InetSocketAddress connectionPort = new InetSocketAddress(
                    Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
            );
            Channel channel = b.bind(connectionPort).sync().channel();
            logger.debug("Server started...");
            channel.closeFuture().await();
        } finally {
            connectionEventGroup.shutdownGracefully();
            dataEventGroup.shutdownGracefully();
        }
    }
    
    public static class ServerChannelInitializer
            extends ChannelInitializer<SocketChannel> {
        
        private ChannelHandler testHandler = new TestHandler();
        private ChannelHandler moveRequestHandler = new MoveRequestHandler();
        private ChannelHandler encoder = new JsonMessageEncoder();
        private ChannelHandler exceptionHandler = new ExceptionHandler();
        private ChannelHandler logInHandler = new LogInHandler();
        private ChannelHandler logOutHandler = new LogOutHandler();
        private ChannelHandler lobbySubscribeHandler = new LobbySubscribeHandler();
        private ChannelHandler enterLobbyHandler = new EnterLobbyHandler();
        private ChannelHandler leaveGameRequestHandler = new LeaveGameRequestHandler();
        private ChannelHandler lobbyUnsubscribeHandler = new LobbyUnsubscribeHandler();
        private ChannelHandler createLobbyHandler = new CreateLobbyHandler();
    
        @Override
        protected void initChannel(SocketChannel ch) {
            try {
                logger.debug("Initializing socket channel...");
                
                ch.pipeline().addFirst("jsonObjectDecoder", new JsonObjectDecoder());
                // initialized here because decoders are not shareable in netty :(
                ch.pipeline().addFirst("decodeHandler", new JsonMessageDecoder());
    
                ch.pipeline().addLast("testHandler", testHandler);
    
                ch.pipeline().addLast("loginHandler", logInHandler);
                ch.pipeline().addLast("logoutHandler", logOutHandler);
                
                ch.pipeline().addLast("lobbySubscribeHandler", lobbySubscribeHandler);
                ch.pipeline().addLast("enterLobbyHandler", enterLobbyHandler);
                ch.pipeline().addLast("lobbyUnsubscribeHandler", lobbyUnsubscribeHandler);
                ch.pipeline().addLast("createLobbyHandler", createLobbyHandler);
                ch.pipeline().addLast("leaveGameRequestHandler", leaveGameRequestHandler);
                
                ch.pipeline().addLast("moveRequestHandler", moveRequestHandler);
    
                ch.pipeline().addLast("encodeHandler", encoder);
                
                ch.pipeline().addLast("cleanupHandler", exceptionHandler);
            } catch (Exception e) {
                logger.error("Exception during channel initialization", e);
                throw e;
            }
        }
    }
}
