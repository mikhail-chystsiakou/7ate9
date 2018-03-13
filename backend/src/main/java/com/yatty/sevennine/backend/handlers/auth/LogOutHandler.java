package com.yatty.sevennine.backend.handlers.auth;

import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.backend.handlers.ConnectHandler;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.model.PlayerRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LogOutHandler extends SimpleChannelInboundHandler<LogOutRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogOutRequest msg) throws Exception {
        Player player = PlayerRegistry.getPlayerByToken(msg.getAuthToken());
        if (player != null) {
            logger.debug("Player {} is logging out", player.getName());
            PlayerRegistry.removePlayerByToken(msg.getAuthToken());
        }
    }
}