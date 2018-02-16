package com.yatty.sevennine.backend.model;

import java.util.*;

public class Game {
    public static final int PLAYERS_NUM = 2;
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

    // TODO: kill me
    public int generateNextMove() {
        card = new Random().nextInt(3) + 1;
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

    // TODO: delete me
    public static Game getGame() {
        if (gameMap.size() == 0 ) {
            return null;
        } else {
            return gameMap.values().iterator().next();
        }
    }
}