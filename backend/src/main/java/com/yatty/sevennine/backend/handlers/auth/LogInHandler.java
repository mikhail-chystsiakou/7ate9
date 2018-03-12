package com.yatty.sevennine.backend.handlers.auth;

import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.backend.handlers.ConnectHandler;
import com.yatty.sevennine.backend.handlers.PlayerMessageSender;
import com.yatty.sevennine.backend.model.PlayerRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LogInHandler extends SimpleChannelInboundHandler<LogInRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogInRequest msg) throws Exception {
        logger.debug("Player {} is logging in", msg.getName());
        LogInResponse response = new LogInResponse();
        if (!PlayerRegistry.checkPlayerRegistered(msg.getName())) {
            PlayerRegistry.registerPlayer(msg.getName(), ctx.channel().remoteAddress());
            response.setSucceed(true);
        } else {
            response.setSucceed(false);
            response.setDescription("Can not log in because player is online already");
        }
        PlayerMessageSender.sendMessage(ctx.channel(), response);
    }
}