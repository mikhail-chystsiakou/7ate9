package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.DisconnectRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisconnectHandler extends SimpleChannelInboundHandler<DisconnectRequest> {
    private static final Logger logger = LoggerFactory.getLogger(DisconnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DisconnectRequest msg) throws Exception {
        logger.debug("Disconnecting...");
        ctx.channel().disconnect().addListener((e) -> {
            if (e.isSuccess()) {
                logger.debug("Disconnected");
            } else {
                logger.debug("Can not disconnect: {}", e.cause());
            }
        }).sync();
    }
}