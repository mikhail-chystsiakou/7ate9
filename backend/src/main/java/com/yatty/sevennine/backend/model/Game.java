package com.yatty.sevennine.backend.model;

import java.util.*;

public class Game {
    private static Map<String, Game> gameMap = new HashMap<>();
    private String id = UUID.randomUUID().toString();
    private static final int PLAYERS_NUM = 2;
    private List<Player> players = new ArrayList<>();

    public static String addGame() {
        Game game = new Game();
        gameMap.put(game.id, game);
        return game.id;
    }

    public static Game getGame(String id) {
        return gameMap.get(id);
    }
}
