package com.yatty.sevennine.backend.handlers.auth;

import com.yatty.sevennine.api.GameResult;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.NewStateNotification;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.LoginedUser;
import com.yatty.sevennine.backend.model.UserRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LogOutHandler extends SimpleChannelInboundHandler<LogOutRequest> {
    private static final Logger logger = LoggerFactory.getLogger(LogOutHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogOutRequest msg) throws Exception {
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("User '{}' is logging out", user.getName());
        UserRegistry.removeUserByToken(msg.getAuthToken());
    }
}