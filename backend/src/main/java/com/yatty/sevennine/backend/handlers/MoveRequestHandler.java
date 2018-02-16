package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.MoveRequest;
import com.yatty.sevennine.api.dto.NewStateEvent;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MoveRequestHandler extends SimpleChannelInboundHandler<MoveRequest> {
    private static final Logger logger = LoggerFactory.getLogger(MoveRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MoveRequest msg) throws Exception {
        logger.trace("Making request...");
        Game game = Game.getGame(msg.getGameId());
        if (game.checkMove(msg.getMove())) {
            NewStateEvent newStateEvent = new NewStateEvent();
            Player player = null;
            InetSocketAddress address = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
            for (Player p : game.getPlayers()) {
                if (p.getRemoteAddress().equals(address)) {
                    player = p;
                    break;
                }
            }
            if (player != null) {
                newStateEvent.setPlayer(player.getName());
                newStateEvent.setNextCard(game.generateNextMove());
            } else {
                logger.warn("Player name is null");
            }
        } else {
            MoveRejectedResponse response = new MoveRejectedResponse();
            response.setMove(msg.getMove());
            ctx.channel().writeAndFlush(response);
        }
    }
}