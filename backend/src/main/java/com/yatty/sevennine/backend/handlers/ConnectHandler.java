package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.ConnectResponse;
import com.yatty.sevennine.api.dto.GameStartedEvent;
import com.yatty.sevennine.backend.model.Deck;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.Player;
import com.yatty.sevennine.backend.util.CardRotator;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class ConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest msg) throws Exception {
        logger.trace("Connecting..");
        InetSocketAddress peerAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();

        Game game = GameRegistry.getFirstGame();
        if (game == null) {
            game = new Game(Game.DEFAULT_PLAYERS_NUM);
            GameRegistry.registerGame(game);
        }

        if (game.isFull()) {
            ConnectResponse response = new ConnectResponse();
            response.setSucceed(false);
            PlayerMessageSender.sendMessage(ctx.channel(), peerAddress, response);
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
        PlayerMessageSender.sendMessage(ctx.channel(), peerAddress, response);

        logger.debug("Game {} is full: {}", game.getId(), game.isFull());
        // game started
        if (game.isFull()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("Unexpected exception during delay", e);
            }
            CardRotator.start(ctx.channel(), game.getPlayers());
            // TODO: move deck to the game itself, it's part of business logic
            List<Player> players = game.getPlayers();
            Deck deck = new Deck(players.size());
            deck.shuffle();
            Card startCard = deck.getStartCard();
            game.setTopCard(startCard);
            for (Player p : players) {
                GameStartedEvent gameStartedEvent = new GameStartedEvent();
                gameStartedEvent.setFirstCard(startCard);
                List<Card> playerCards = deck.pullCards();
                gameStartedEvent.setPlayerCards(playerCards);
                logger.debug("PlayerCards for {}: {}", player.getGame(), playerCards.size());
                game.setPlayerCardsNum(playerCards.size());
                PlayerMessageSender.sendMessage(ctx.channel(), p, gameStartedEvent);
            }
        }
    }
}