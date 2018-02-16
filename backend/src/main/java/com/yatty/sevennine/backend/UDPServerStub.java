package com.yatty.sevennine.backend;

import com.yatty.sevennine.backend.handlers.ConnectHandler;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
            InetSocketAddress connectionPort = new InetSocketAddress(
                    Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
            );
            System.out.println("Server started...");
            b.bind(connectionPort).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static class ServerChannelInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ch.pipeline().addFirst("decodeHandler", new JsonMessageDecoder());
            ch.pipeline().addLast("connectHandler", new ConnectHandler());
            ch.pipeline().addLast("encodeHandler", new JsonMessageEncoder());
        }
    }
}
