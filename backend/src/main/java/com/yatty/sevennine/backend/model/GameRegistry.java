package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.dto.lobby.PublicLobbyInfo;
import com.yatty.sevennine.backend.exceptions.logic.LobbyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameRegistry {
    private static final Logger logger = LoggerFactory.getLogger(GameRegistry.class);
    private static final Map<String, Game> lobbyMap = new ConcurrentHashMap<>();
    private static final Map<String, Game> runningGames = new ConcurrentHashMap<>();

    public static void registerLobby(Game game) {
        logger.debug("Registering new logic '{}' with id '{}'",
                game.getName(), game.getId());
        lobbyMap.put(game.getId(), game);
    }
    
    public static void gameStarted(String lobbyId) {
        Game lobby = lobbyMap.get(lobbyId);
        if (lobby != null) {
            lobbyMap.remove(lobbyId);
            runningGames.put(lobbyId, lobby);
            logger.debug("Game '{}' (id '{}') started",
                    lobby.getName(), lobby.getId());
        } else {
            throw new LobbyNotFoundException("Failed to start game. ", lobbyId);
            
        }
    }
    
    public static void gameFinished(String gameId) {
        if (runningGames.containsKey(gameId)) {
            logger.debug("Game '{}' (id '{}') finished",
                    runningGames.get(gameId).getName(), gameId);
            runningGames.remove(gameId);
        } else {
            throw new LobbyNotFoundException("Failed to finish game", gameId);
        }
    }
    
    @Nonnull
    public static Game getLobbyById(String lobbyId) {
        if (lobbyMap.containsKey(lobbyId)) {
            return lobbyMap.get(lobbyId);
        } else {
            throw new LobbyNotFoundException(lobbyId);
        }
    }

    @Nonnull
    public static Game getGameById(String gameId) {
        if (runningGames.containsKey(gameId)) {
            return runningGames.get(gameId);
        } else {
            throw new LobbyNotFoundException(gameId);
        }
    }
    
    public static Collection<Game> getLobbyList() {
        return Collections.unmodifiableCollection(lobbyMap.values());
    }
    
    public static Collection<PublicLobbyInfo> getLobbyListPublicInfo() {
        return getLobbyList()
                .stream()
                .map(Game::getPublicLobbyInfo)
                .collect(Collectors.toList());
    }
}
