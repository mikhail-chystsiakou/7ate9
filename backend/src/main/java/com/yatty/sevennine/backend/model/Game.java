package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.backend.exceptions.SevenNineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.*;

/**
 * Represents the stateful game object.
 *
 * @version 26/02/18
 * @author Mike
 */
public class Game {
    public static final Logger logger = LoggerFactory.getLogger(Game.class);
    public static final int INITIAL_PLAYER_CARD_NUM = 10;   // TODO: use constant from Deck class
    public static final int DEFAULT_PLAYERS_NUM = 2;   // TODO: to be deleted after lobby implementation

    private String id = UUID.randomUUID().toString();
    private List<Player> players = new ArrayList<>();
    private Card topCard;
    private int moveNumber;
    private int playersNum;

    public Game(int playersNum) {
        this.playersNum = playersNum;
    }

    /**
     * Adds player to players list.
     *
     * @param   player              object to add
     * @return  true                if the game is full and next player can not be added
     * @throws  SevenNineException  on attempt to add player to the full game
     */
    public boolean addPlayer(Player player) {
        if (players.size() >= playersNum) {
            throw new SevenNineException(
                MessageFormatter.format(
                    "Can not add player '{}' to game '{}': the game is full",
                    player.getGame(),
                    this.id
                ).getMessage());
        }

        players.add(player);
        return players.size() >= playersNum;
    }

    public Player getWinner() {
        Player winner = players.get(0);
        for (Player p : players) {
            if (winner.getScore() < p.getScore()) winner = p;
        }
        return winner;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public String getId() {
        return id;
    }

    public boolean acceptMove(Card move) {
        logger.debug("Accepting move {} for topCard {}", move, topCard);

        int greaterVariant = topCard.getValue() + topCard.getModifier();
        if (greaterVariant > 10) greaterVariant -= 10;
        int lesserVariant = topCard.getValue() - topCard.getModifier();
        if (lesserVariant < 0) lesserVariant += 10;

        boolean validMove = move.getValue() == greaterVariant
                || move.getValue() == lesserVariant;

        if (validMove) {
            topCard = move;
            moveNumber++;
        }
        return validMove;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public boolean isFull() {
        return players.size() >= playersNum;
    }

    public void setTopCard(Card topCard) {
        logger.debug("Game top card updated: {}", topCard);
        this.topCard = topCard;
    }
}