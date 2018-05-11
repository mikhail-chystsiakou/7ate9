package com.yatty.sevennine.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SevenAteNineClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelHandler encoder;
    private ChannelHandler decoder;
    private ChannelHandler exceptionHandler;
    private List<ChannelHandler> handlers = new ArrayList<>(16);
    
    public SevenAteNineClientChannelInitializer() {
    
    }
    
    public SevenAteNineClientChannelInitializer(SevenAteNineClientChannelInitializer clone) {
        setDecoder(clone.decoder);
        setEncoder(clone.encoder);
        setExceptionHandler(clone.exceptionHandler);
        this.handlers.addAll(clone.handlers);
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addFirst("jsonObjectDecoder", new JsonObjectDecoder());
        ch.pipeline().addLast(decoder);
        for (ChannelHandler handler : handlers) {
            ch.pipeline().addLast(handler);
        }
        ch.pipeline().addLast(encoder);
        if (exceptionHandler != null) {
            ch.pipeline().addLast(exceptionHandler);
        }
    }
    
    public void setEncoder(ChannelHandler encoder) {
        this.encoder = encoder;
    }
    
    public void setDecoder(ChannelHandler decoder) {
        this.decoder = decoder;
    }
    
    public void setExceptionHandler(ChannelHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    public <T> void addHandler(Consumer<T> handler, Class<T> messageType) {
        this.handlers.add(new MessageHandler<>(handler, messageType));
    }
}
