package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.DisconnectRequest;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.util.CardRotator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisconnectHandler extends SimpleChannelInboundHandler<DisconnectRequest> {
    private static final Logger logger = LoggerFactory.getLogger(DisconnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DisconnectRequest msg) throws Exception {
        logger.debug("Disconnecting...");
        GameRegistry.deleteGames();
        CardRotator.stop();
    }
}