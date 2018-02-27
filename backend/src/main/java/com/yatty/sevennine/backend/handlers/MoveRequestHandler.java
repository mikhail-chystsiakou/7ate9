package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.GameResult;
import com.yatty.sevennine.api.PlayerResult;
import com.yatty.sevennine.api.dto.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.MoveRequest;
import com.yatty.sevennine.api.dto.NewStateEvent;
import com.yatty.sevennine.backend.exceptions.security.UnauthorizedAccessException;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.util.CardRotator;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Processes users' moves.
 * <ul>
 *     <li>If move was illegal, sends {@link MoveRejectedResponse} back to
 *     user.</li>
 *
 *     <li>If move was valid, sends {@link NewStateEvent} for all users with
 *     name of user, that made right move. Also checks if game is over, and if
 *     true, {@link NewStateEvent#lastMove} flag</li>
 * </ul>
 *
 * @author Mike
 * @version 17/02/18
 */
public class MoveRequestHandler extends SimpleChannelInboundHandler<MoveRequest> {
    private static final Logger logger = LoggerFactory.getLogger(MoveRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MoveRequest msg) throws Exception {
        logger.debug("Got move request: {}", msg.getMove());
        // TODO: get game by id from request during lobby implementation
        Game game = GameRegistry.getFirstGame();
        if (game == null) {
            game = new Game(Game.DEFAULT_PLAYERS_NUM);
            GameRegistry.registerGame(game);
        }

        InetSocketAddress clientAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
        Player moveAuthor = game.getPlayers().stream().filter(
                p -> p.getRemoteAddress().equals(clientAddress)
        ).findFirst().orElseThrow(() -> {
            UnauthorizedAccessException exception = new UnauthorizedAccessException();
            exception.setRemoteAddress(clientAddress);
            return exception;
        });

        if (moveAuthor == null) {
            logger.warn("Address {} is not authenticated, ignoring message {}", clientAddress, msg);
            processUnregisteredMove(ctx.channel(), clientAddress, msg);
        } else if (game.acceptMove(msg.getMove())) {
            processRightMove(ctx.channel(), clientAddress, game, moveAuthor, msg.getMove());
        } else {
            processWrongMove(ctx.channel(), clientAddress, msg);
        }
    }

    private void processRightMove(Channel channel, InetSocketAddress address,
                                  Game game, Player roundWinner, Card move) {
        NewStateEvent newStateEvent = new NewStateEvent();
        newStateEvent.setMoveWinner(roundWinner.getName());
        CardRotator.refresh();

        roundWinner.incScore();
        if (roundWinner.getScore() > Game.INITIAL_PLAYER_CARD_NUM - 2) {
            newStateEvent.setLastMove(true);
            Player winner = game.getWinner();
            GameResult gameResult = new GameResult();
            gameResult.setWinner(winner.getName());
            for (Player p : game.getPlayers()) {
                PlayerResult playerResult = new PlayerResult();
                playerResult.setPlayerName(p.getName());
                playerResult.setCardsLeft(Game.INITIAL_PLAYER_CARD_NUM - p.getScore());
                gameResult.addScore(playerResult);
            }
            newStateEvent.setGameResult(gameResult);

            GameRegistry.deleteGameById(game.getId());
            CardRotator.stop();

            // TODO: set other fields after NewStateEvent update
        } else {
            newStateEvent.setNextCard(move);
            game.setTopCard(move);
        }
        PlayerMessageSender.broadcast(channel, game.getPlayers(), newStateEvent);
    }

    private void processWrongMove(Channel channel, InetSocketAddress address, MoveRequest moveRequestMsg) {
        MoveRejectedResponse response = new MoveRejectedResponse();
        response.setMove(moveRequestMsg.getMove());

        logger.debug("Move {} rejected for {}", moveRequestMsg.getMove(), address);
        PlayerMessageSender.sendMessage(channel, address, response);
    }

    private void processUnregisteredMove(Channel channel, InetSocketAddress address, MoveRequest moveRequestMsg) {
        MoveRejectedResponse response = new MoveRejectedResponse();
        response.setMove(moveRequestMsg.getMove());

        logger.debug("Move {} rejected for {}", moveRequestMsg.getMove(), address);
        PlayerMessageSender.sendMessage(channel, address, response);
    }
}