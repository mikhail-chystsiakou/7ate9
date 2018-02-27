package com.yatty.sevennine.backend.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameRegistry {
    private static final Map<String, Game> gameMap = new ConcurrentHashMap<>();

    // TODO: to be deleted after lobby implementation
    public static Game getFirstGame() {
        if (gameMap.values().size() == 0) {
            return null;
        }
        return gameMap.values().iterator().next();
    }

    // TODO: to be deleted after lobby implementation
    public static void deleteGames() {
        if (gameMap.values().size() > 0) {
            gameMap.clear();
        }
    }

    public static void registerGame(Game game) {
        gameMap.put(game.getId(), game);
    }

    public static Game getGameById(String gameId) {
        return gameMap.get(gameId);
    }

    public static void deleteGameById(String gameId) {
        gameMap.remove(gameId);
    }
}
