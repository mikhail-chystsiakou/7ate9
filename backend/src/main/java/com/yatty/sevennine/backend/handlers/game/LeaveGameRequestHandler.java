package com.yatty.sevennine.backend.handlers.game;

import com.yatty.sevennine.api.dto.game.MoveRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaveGameRequestHandler extends SimpleChannelInboundHandler<MoveRequest> {
    private static final Logger logger = LoggerFactory.getLogger(LeaveGameRequestHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MoveRequest msg) throws Exception {
        logger.debug("Got game leave request: {}", msg.getMove());
        // TODO: update player score
        // TODO: send notification to other players
    }
}