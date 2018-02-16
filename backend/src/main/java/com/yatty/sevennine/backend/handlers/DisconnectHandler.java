package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.DisconnectRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DisconnectHandler extends SimpleChannelInboundHandler<DisconnectRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DisconnectRequest msg) throws Exception {
        System.out.println("Disconnecting...");
        ctx.channel().disconnect();
    }
}