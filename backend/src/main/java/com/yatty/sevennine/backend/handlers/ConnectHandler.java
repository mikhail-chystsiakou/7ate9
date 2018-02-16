package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.ConnectResponse;
import com.yatty.sevennine.api.dto.GameStartedEvent;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;

public class ConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {

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
        Player player = new Player(msg.getName());
        player.setRemoteAddress(ctx.channel());
        Game game = Game.getGame();
        if (game == null) {
            game = Game.addGame();
        }

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
        game.addPlayer(player);
        if (game.getPlayersNum() == Game.PLAYERS_NUM) {
            System.out.println("Sending game started event");
            game.getPlayers().forEach((p) -> {
                GameStartedEvent gameStartedEvent = new GameStartedEvent(Game.generateNextMove());
                p.getRemoteAddress().writeAndFlush(gameStartedEvent).addListener((e) -> {
                    if (e.isSuccess()) {
                        System.out.println("Game start message sent to "+ player.getName());
                    } else {
                        e.cause().printStackTrace();
                    }
                });
            });
        }
    }
}