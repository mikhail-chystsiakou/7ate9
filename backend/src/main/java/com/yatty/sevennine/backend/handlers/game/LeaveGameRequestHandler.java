package com.yatty.sevennine.backend.handlers.game;

import com.yatty.sevennine.api.GameResult;
import com.yatty.sevennine.api.dto.game.LeaveGameRequest;
import com.yatty.sevennine.api.dto.game.MoveRequest;

import com.yatty.sevennine.api.dto.game.NewStateNotification;
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

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class LeaveGameRequestHandler extends SimpleChannelInboundHandler<LeaveGameRequest> {
    private static final Logger logger = LoggerFactory.getLogger(LeaveGameRequestHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LeaveGameRequest msg) throws Exception {
        // TODO: update player score
        // TODO: send notification to other players
        
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        logger.debug("Got game leave request: {}", user.getUser().getGeneratedLogin());
        Game game = GameRegistry.getGameById(msg.getGameId());
        game.removePlayer(user);
        if (game.isFinished()) {
            if (game.getWinner() != null) {
                NewStateNotification newStateNotification = new NewStateNotification();
                newStateNotification.setMoveWinner(game.getWinner().getLoginedUser().getUser().getGeneratedLogin());
                newStateNotification.setMoveNumber(game.getMoveNumber());
                newStateNotification.setLastMove(true);
    
                GameResult gameResult = new GameResult();
                gameResult.setWinner(game.getWinner().getLoginedUser().getUser().getGeneratedLogin());
                game.getPlayers().forEach(p -> gameResult.addScore(p.getResult()));
                newStateNotification.setGameResult(gameResult);
    
                game.getLoginedUsers().forEach(u -> u.getChannel().writeAndFlush(newStateNotification));
            }
            
            GameRegistry.gameFinished(game.getId());
        }
    }
}