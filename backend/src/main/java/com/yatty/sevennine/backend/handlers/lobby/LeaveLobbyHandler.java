package com.yatty.sevennine.backend.handlers.lobby;

import com.yatty.sevennine.api.dto.lobby.*;
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
public class LeaveLobbyHandler extends SimpleChannelInboundHandler<LeaveLobbyRequest> {
    private static final Logger logger = LoggerFactory.getLogger(com.yatty.sevennine.backend.handlers.lobby.LeaveLobbyHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LeaveLobbyRequest msg) throws Exception {
        // remove from REGISTERED players
        // if there is no players, delete lobby
        // else send lobby updated notification to other registered players
        // send lobbies list updated
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("Player '{}' is leaving lobby '{}'",
                user.getUser().getGeneratedLogin(), msg.getLobbyId()
        );
        
        Game lobby = GameRegistry.getLobbyById(msg.getLobbyId());
        lobby.unregister(user);
        
        if (lobby.getRegisteredPlayers().size() < 1) {
            GameRegistry.removeLobby(lobby.getId());
        } else {
            LobbyStateChangedNotification newLobbyState =
                    new LobbyStateChangedNotification(lobby.getPrivateLobbyInfo());
            lobby.getRegisteredPlayers().forEach(p -> {
                p.getChannel().writeAndFlush(newLobbyState);
            });
        }
    
        GameRegistry.sendLobbyListUpdateNotification();
    }
}
