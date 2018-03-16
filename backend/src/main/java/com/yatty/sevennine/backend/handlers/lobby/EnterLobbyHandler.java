package com.yatty.sevennine.backend.handlers.lobby;

import com.yatty.sevennine.api.dto.game.GameStartedEvent;
import com.yatty.sevennine.api.dto.lobby.*;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.LoginedUser;
import com.yatty.sevennine.backend.model.UserRegistry;
import com.yatty.sevennine.backend.util.CardRotator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class EnterLobbyHandler extends SimpleChannelInboundHandler<EnterLobbyRequest> {
    private static final Logger logger = LoggerFactory.getLogger(EnterLobbyHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnterLobbyRequest msg) throws Exception {
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("Player '{}' is entering lobby '{}'",
                user.getName(), msg.getLobbyId()
        );
    
        Game lobby = GameRegistry.getLobbyById(msg.getLobbyId());
        lobby.addPlayer(user);
        user.setChannel(ctx.channel());
        
        UserRegistry.addSubscriber(msg.getAuthToken());
        
        EnterLobbyResponse response = new EnterLobbyResponse();
        response.setPrivateLobbyInfo(lobby.getPrivateLobbyInfo());
        
        ctx.channel().writeAndFlush(response).sync();
        
        if (lobby.isFull()) {
            lobby.giveOutCards();
            
            GameRegistry.gameStarted(lobby.getId());
            
            lobby.getPlayers().forEach(p -> {
                GameStartedEvent gameStartedEvent = new GameStartedEvent();
                gameStartedEvent.setFirstCard(lobby.getTopCard());
                gameStartedEvent.setPlayerCards(p.getCards());
                gameStartedEvent.setLobbyId(msg.getLobbyId());
                
                p.getLoginedUser().getChannel().writeAndFlush(gameStartedEvent);
            });
            
            CardRotator.start(lobby);
        }
    }
}