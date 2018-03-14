package com.yatty.sevennine.backend.handlers.lobby;

import com.yatty.sevennine.api.dto.lobby.LobbySubscribeRequest;
import com.yatty.sevennine.backend.model.LoginedUser;
import com.yatty.sevennine.backend.model.UserRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LobbyUnsubscribeHandler extends SimpleChannelInboundHandler<LobbySubscribeRequest> {
    private static final Logger logger = LoggerFactory.getLogger(LobbyUnsubscribeHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LobbySubscribeRequest msg) throws Exception {
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("Player '{}' is unsubscribing for updates", user.getName());
        UserRegistry.removeSubscriber(msg.getAuthToken());
    }
}