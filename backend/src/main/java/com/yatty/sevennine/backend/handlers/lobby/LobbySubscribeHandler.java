package com.yatty.sevennine.backend.handlers.lobby;

import com.yatty.sevennine.api.dto.lobby.LobbySubscribeRequest;
import com.yatty.sevennine.backend.handlers.ConnectHandler;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.model.PlayerRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LobbySubscribeHandler extends SimpleChannelInboundHandler<LobbySubscribeRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LobbySubscribeRequest msg) throws Exception {
        Player player = PlayerRegistry.getPlayerByToken(msg.getAuthToken());
        if (player != null) {
            logger.debug("Player {} is subscribing for updates", player.getName());
            player.setSubscriber(true);
            // TODO: send current lobby status
//            PlayerMessageSender.sendMessage(ctx.channel(), response);
        } else {
            logger.debug("Lobby subscribe request for unregistered auth token: {}", msg.getAuthToken());
        }
    }
}