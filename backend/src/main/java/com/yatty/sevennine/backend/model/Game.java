package com.yatty.sevennine.backend.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Represents the single (just for now) stateful game object.
 *
 * @version 17/02/18
 * @author Mike
 */
public class Game {
    public static final Logger logger = LoggerFactory.getLogger(Game.class);
    public static final int PLAYERS_NUM = 2;
    public static final int INITIAL_PLAYER_CARD_NUM = 10;   // TODO: use constant from Deck class
    private static Map<String, Game> gameMap = new HashMap<>();

    private String id = UUID.randomUUID().toString();
    private List<Player> players = new ArrayList<>();
    private int card;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getPlayersNum() {
        return players.size();
    }

    public String getId() {
        return id;
    }

    public int generateNextMove() {
        card = new Random().nextInt(3) + 1;
        logger.debug("Next game card: {}", card);
        return card;
    }

    public static Game addGame() {
        Game game = new Game();
        gameMap.put(game.id, game);
        return game;
    }

    public boolean checkMove(int move) {
        return card == move;
    }

    public static Game getGame(String id) {
        return gameMap.get(id);
    }

    // tmp first spring stub
    public static Game getGame() {
        if (gameMap.size() == 0 ) {
            return null;
        } else {
            return gameMap.values().iterator().next();
        }
    }

    public void sendMessageToPlayers()

    public static void resetGame() {
        gameMap.clear();
    }
}
