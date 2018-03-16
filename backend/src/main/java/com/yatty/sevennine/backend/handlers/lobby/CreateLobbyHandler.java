package com.yatty.sevennine.backend.handlers.lobby;

import com.yatty.sevennine.api.dto.lobby.CreateLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.CreateLobbyResponse;
import com.yatty.sevennine.api.dto.lobby.LobbyListUpdatedNotification;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.LoginedUser;
import com.yatty.sevennine.backend.model.UserRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@ChannelHandler.Sharable
public class CreateLobbyHandler extends SimpleChannelInboundHandler<CreateLobbyRequest> {
    private static final Logger logger = LoggerFactory.getLogger(CreateLobbyHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateLobbyRequest msg) throws Exception {
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("User '{}' is creating lobby '{}'", user.getName(), msg.getLobbyName());
        
        Game newLobby = new Game(
                msg.getLobbyName(),
                msg.getMaxPlayersNumber()
        );
        newLobby.addPlayer(user);
        GameRegistry.registerLobby(newLobby);
        
        
        CreateLobbyResponse response = new CreateLobbyResponse();
        response.setLobbyId(newLobby.getId());
        
        ctx.channel().writeAndFlush(response).sync();
        
        LobbyListUpdatedNotification notification = new LobbyListUpdatedNotification();
        notification.setLobbyList(GameRegistry.getLobbyListPublicInfo());
        Collection<LoginedUser> lobbiesSubscribers = UserRegistry.getSubscribers();
        
        lobbiesSubscribers.forEach(u -> u.getChannel().writeAndFlush(notification));
    }
}