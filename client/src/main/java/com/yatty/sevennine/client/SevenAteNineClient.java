package com.yatty.sevennine.client;

import com.yatty.sevennine.api.dto.KeepAliveRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class SevenAteNineClient {
    private static final Logger logger = LoggerFactory.getLogger(SevenAteNineClient.class);
    private InetSocketAddress serverAddress;
    private volatile EventLoopGroup eventLoopGroup;
    private ChannelInitializer<SocketChannel> channelInitializer;
    private volatile Channel aliveChannel;
    private volatile boolean keepAlive;
    
    SevenAteNineClient(InetSocketAddress serverAddress, ChannelInitializer<SocketChannel> channelInitializer) {
        this.serverAddress = serverAddress;
        this.channelInitializer = channelInitializer;
    }
    
    public void start() throws InterruptedException {
        if (eventLoopGroup != null) {
            throw new IllegalStateException("Client is running already!");
        }
        eventLoopGroup = new NioEventLoopGroup(1);
        
        connect();
    }
    
    public void sendMessage(Object message) {
        sendMessage(message, keepAlive);
    }
    
    public void sendMessage(Object message, boolean keepAlive) {
        if (aliveChannel == null) {
            throw new IllegalStateException("Can not send message because client is not started yet");
        }
        this.keepAlive = keepAlive;
        aliveChannel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                logger.error("Failed to send message: {}", future.cause());
            }
        });
    }
    
    public void stop() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully().addListener((e) -> {
                if (e.isSuccess()) {
                    logger.debug("SevenAteNineClient shutdown succeed");
                    eventLoopGroup = null;
                } else {
                    logger.error("Failed to SevenAteNineClient gracefully");
                }
            });
        } else {
            logger.warn("Can not stop SevenAteNineClient: client wasn't started");
        }
    }
    
    private void connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(serverAddress)
                .handler(channelInitializer);
        aliveChannel = bootstrap.connect().sync().channel();
        aliveChannel.closeFuture().addListener(e -> {
            if (keepAlive) {
                logger.debug("Connection closed, reopening...");
                connect();
                aliveChannel.writeAndFlush(new KeepAliveRequest());
            } else {
                logger.debug("Connection closed, do not reopen");
            }
        });
    }
    
    public <T> void addMessageHandler(Consumer<T> handler, Class<T> messageType) {
        aliveChannel.pipeline().addLast(new MessageHandler<>(handler, messageType));
    }
}
