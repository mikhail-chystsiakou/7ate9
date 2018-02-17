package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.ConnectResponse;
import com.yatty.sevennine.api.dto.GameStartedEvent;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest msg) throws Exception {
        logger.trace("Connecting..");
        InetSocketAddress peerAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
        ctx.channel().connect(peerAddress).addListener((e) -> {
            if (e.isSuccess()) {
                logger.debug("Connected to {}", peerAddress);
            } else {
                logger.warn("Failed to connect: ", e.cause());
            }
        }).sync();
        Game game = Game.getGame();
        if (game == null) {
            game = Game.addGame();
        }

        if (game.getPlayersNum() >= Game.PLAYERS_NUM) {
            ConnectResponse response = new ConnectResponse();
            response.setSucceed(false);
            ctx.channel().writeAndFlush(response).addListener((e) -> {
                if (e.isSuccess()) {
                    logger.debug("Connect response {} sent", response);
                } else {
                    e.cause().printStackTrace();
                }
            }).sync();
            return;
        }

        Player player = new Player(msg.getName());
        player.setRemoteAddress(peerAddress);
        player.setGame(game);
        game.addPlayer(player);

        logger.debug("Player {} added from message {}", player, msg);

        ConnectResponse response = new ConnectResponse();
        response.setGameId(game.getId());
        response.setSucceed(true);
        ctx.channel().writeAndFlush(response).addListener((e) -> {
            if (e.isSuccess()) {
                logger.debug("Connect response {} sent", response);
            } else {
                e.cause().printStackTrace();
            }
        }).sync();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.warn("Unexpected exception during delay", e);
        }

        // game started
        if (game.getPlayersNum() == Game.PLAYERS_NUM) {
            GameStartedEvent gameStartedEvent = new GameStartedEvent(game.generateNextMove());
            game.getPlayers().forEach((p) -> {
                // TODO fix for real multi-threading
                try {
                    ctx.channel().disconnect().sync();
                    ctx.channel().connect(p.getRemoteAddress());
                    ctx.channel().writeAndFlush(gameStartedEvent).addListener((e) -> {
                        if (e.isSuccess()) {
                            logger.debug("Game start message {} sent to '{}' ({})", gameStartedEvent, p.getName(), p.getRemoteAddress());
                        } else {
                            logger.warn("Can not send message to remote peer: {}", e);
                            e.cause().printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    logger.warn("Unexpected exception: {}", e);
                }
            });
        }
    }
}