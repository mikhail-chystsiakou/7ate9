package com.yatty.sevennine.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

class MessageHandler<T> extends SimpleChannelInboundHandler<T> {
    private Consumer<T> consumer;
    
    MessageHandler(Consumer<T> consumer, Class<T> clazz) {
        super(clazz);
        this.consumer = consumer;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        consumer.accept(msg);
    }
}