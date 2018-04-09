package com.yatty.sevennine.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SevenAteNineClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(SevenAteNineClientFactory.class);
    private SevenAteNineClientChannelInitializer channelInitializer = new SevenAteNineClientChannelInitializer();
    
    public SevenAteNineClient getClient(InetSocketAddress serverAddress) {
        return new SevenAteNineClient(
                serverAddress,
                new SevenAteNineClientChannelInitializer(channelInitializer)
        );
    }
    
    public SynchronousClient getSynchronousClient(InetSocketAddress serverAddress) {
        return new SynchronousClient(
                serverAddress,
                new SevenAteNineClientChannelInitializer(channelInitializer)
        );
    }
    
    public void setCustomEncoder(Function<Object, String> encoder) {
        channelInitializer.setEncoder(new MessageToMessageEncoder<Object>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
                out.add(encoder.apply(msg));
            }
        });
    }
    
    public void setCustomDecoder(Function<String, Object> decoder) {
        channelInitializer.setDecoder(new MessageToMessageDecoder<ByteBuf>() {
            protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
                String data = msg.readBytes(msg.readableBytes()).toString(StandardCharsets.UTF_8);
                out.add(decoder.apply(data));
            }
        });
    }
    
    public <T> void addMessageHandler(Consumer<T> handler, Class<T> messageType) {
        channelInitializer.addHandler(handler, messageType);
    }
    
    public void setExceptionHandler(Consumer<Throwable> handler) {
        channelInitializer.setExceptionHandler(new ChannelDuplexHandler() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                handler.accept(cause);
            }
        });
    }
}
