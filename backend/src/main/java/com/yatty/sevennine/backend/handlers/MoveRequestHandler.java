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
                for(Player p : game.getPlayers()) {
                    ctx.channel().disconnect().sync();
                    ctx.channel().connect(p.getRemoteAddress());
                    ctx.channel().writeAndFlush(newStateEvent).addListener((e) -> {
                        if (e.isSuccess()) {
                            logger.debug("New state message sent to '{}' ({})", p.getName(), p.getRemoteAddress());
                        } else {
                            logger.warn("Can not send message to remote peer: {}", e);
                        }
                    }).sync();
                }
            } else {
                logger.warn("Can not make move because player is not registered in the game");
            }
        } else {
            MoveRejectedResponse response = new MoveRejectedResponse();
            response.setMove(msg.getMove());
            logger.debug("Move {} rejected", msg.getMove());
            ctx.channel().writeAndFlush(response);
        }
    }
}