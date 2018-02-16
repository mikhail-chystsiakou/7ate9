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
import java.nio.channels.Channel;

public class ConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest msg) throws Exception {
        System.out.println("Connecting..");
        InetSocketAddress peerAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
        System.out.println("Got message from: " + peerAddress);
        ctx.channel().connect(peerAddress).addListener((e) -> {
            if (e.isSuccess()) {
                System.out.println("Connected to peer");
            } else {
                System.out.println("Failed to connect");
                e.cause().printStackTrace();
            }
        }).sync();
        Game game = Game.getGame();
        if (game == null) {
            game = Game.addGame();
        }

        Player player = new Player(msg.getName());
        player.setRemoteAddress(peerAddress);
        player.setGame(game);
        game.addPlayer(player);

        ConnectResponse response = new ConnectResponse();
        response.setGameId(game.getId());
        response.setSucceed(true);
        ctx.channel().writeAndFlush(response).addListener((e) -> {
            if (e.isSuccess()) {
                System.out.println("Response sent");
            } else {
                e.cause().printStackTrace();
            }
        }).sync();

        // game started
        if (game.getPlayersNum() == Game.PLAYERS_NUM) {
            System.out.println("Sending game started event");
            game.getPlayers().forEach((p) -> {
                GameStartedEvent gameStartedEvent = new GameStartedEvent(p.getGame().generateNextMove());
                // TODO fix for real multi-threading
                try {
                    ctx.channel().disconnect().sync();
                    ctx.channel().connect(p.getRemoteAddress());
                    ctx.channel().writeAndFlush(gameStartedEvent).addListener((e) -> {
                        if (e.isSuccess()) {
                            System.out.println("Game start message sent to "+ player.getName());
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