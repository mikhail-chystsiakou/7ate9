package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.MoveRequest;
import com.yatty.sevennine.api.dto.NewStateEvent;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.Player;
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
        logger.trace("Player making move...");
        // TODO: get game by id from request
        Game game = Game.getGame();
        if (game == null) {
            logger.error("Fuck, game is null");
            return;
        }

        InetSocketAddress clientAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
        Player roundWinner = null;
        for (Player p : game.getPlayers()) {
            if (p.getRemoteAddress().equals(clientAddress)) {
                roundWinner = p;
                break;
            }
        }
        if (roundWinner == null) {
            logger.warn("Address {} is not authenticated, ignoring message {}", clientAddress, msg);
            return;
        }

        if (game.checkMove(msg.getMove())) {
            processRightMove(ctx.channel(), clientAddress, game, roundWinner);
        } else {
            processWrongMove(ctx.channel(), clientAddress, msg);
        }
    }

    private void processRightMove(Channel channel, InetSocketAddress address, Game game, Player roundWinner) {
        NewStateEvent newStateEvent = new NewStateEvent();
        newStateEvent.setMoveWinner(roundWinner.getName());

        roundWinner.incScore();
        if (roundWinner.getScore() >= Game.INITIAL_PLAYER_CARD_NUM) {
            newStateEvent.setLastMove(true);
            // TODO: set other fields after NewStateEvent update
        } else {
            newStateEvent.setNextCard(game.generateNextMove());
        }
        PlayerMessageSender.broadcast(channel, game.getPlayers(), newStateEvent);
    }

    private void processWrongMove(Channel channel, InetSocketAddress address, MoveRequest moveRequestMsg) {
        MoveRejectedResponse response = new MoveRejectedResponse();
        response.setMove(moveRequestMsg.getMove());

        logger.debug("Move {} rejected for {}", moveRequestMsg.getMove(), address);
        PlayerMessageSender.sendMessage(channel, address, moveRequestMsg);
    }
}