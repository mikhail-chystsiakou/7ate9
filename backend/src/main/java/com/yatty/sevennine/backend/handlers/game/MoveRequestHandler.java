package com.yatty.sevennine.backend.handlers.game;

import com.yatty.sevennine.api.GameResult;
import com.yatty.sevennine.api.dto.game.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.game.MoveRequest;
import com.yatty.sevennine.api.dto.game.NewStateNotification;
import com.yatty.sevennine.backend.exceptions.security.GameAccessException;
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

/**
 * Processes users' moves.
 * <ul>
 *     <li>If move was illegal, sends {@link MoveRejectedResponse} back to
 *     user.</li>
 *
 *     <li>If move was valid, sends {@link NewStateNotification} for all users with
 *     name of user, that made right move. Also checks if game is over, and if
 *     true, {@link NewStateNotification#lastMove} flag</li>
 * </ul>
 *
 * @author Mike
 * @version 14/03/18
 */
@ChannelHandler.Sharable
public class MoveRequestHandler extends SimpleChannelInboundHandler<MoveRequest> {
    private static final Logger logger = LoggerFactory.getLogger(MoveRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                MoveRequest msg) throws Exception {
        logger.debug("Got move request: {}", msg.getMove());
        LoginedUser user = UserRegistry.checkAndGetLoginedUser(msg.getAuthToken());
        Game game = GameRegistry.getGameById(msg.getGameId());
        if (!game.checkUserJoined(user)) {
            throw new GameAccessException(user, game);
        }

        if (game.acceptMove(msg.getMove(), user)) {
            processRightMove(game, user, msg);
        } else {
            processWrongMove(user, msg);
        }
    }

    private void processRightMove(Game game, LoginedUser moveAuthor,
                                  MoveRequest moveRequestMsg) {
        NewStateNotification newStateNotification = new NewStateNotification();
        newStateNotification.setMoveWinner(moveAuthor.getName());
        newStateNotification.setMoveNumber(game.getMoveNumber());
        
        if (game.isFinished()) {
            newStateNotification.setLastMove(true);
            
            GameResult gameResult = new GameResult();
            gameResult.setWinner(game.getWinner().getLoginedUser().getName());
            game.getPlayers().forEach(p -> gameResult.addScore(p.getResult()));
            
            newStateNotification.setGameResult(gameResult);

            GameRegistry.gameFinished(game.getId());
            CardRotator.stop(game.getId());
        } else {
            newStateNotification.setNextCard(moveRequestMsg.getMove());
            CardRotator.refresh(game.getId());
        }
        
        game.getLoginedUsers().forEach(u -> u.getChannel().writeAndFlush(newStateNotification));
    }

    private void processWrongMove(LoginedUser moveAuthor, MoveRequest moveRequestMsg) {
        MoveRejectedResponse response = new MoveRejectedResponse();
        response.setMove(moveRequestMsg.getMove());

        logger.debug("Move {} rejected for '{}'", moveRequestMsg.getMove(), moveAuthor.getName());
        moveAuthor.getChannel().writeAndFlush(response);
    }
}