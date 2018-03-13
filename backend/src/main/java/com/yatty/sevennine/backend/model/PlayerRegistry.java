package com.yatty.sevennine.backend.model;

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRegistry {
    private static final Map<String, Player> playersMap = new ConcurrentHashMap<>();

    public static String registerPlayer(String name, SocketAddress address) {
        Player player = new Player(name);
        player.setRemoteAddress(address);
        String playerAuthToken = UUID.randomUUID().toString();
        player.setAuthToken(playerAuthToken);
        playersMap.put(playerAuthToken, player);
        return playerAuthToken;
    }
    
    public static Player getPlayerByToken(String authToken) {
        return playersMap.get(authToken);
    }
    
    public static void removePlayerByToken(String authToken) {
        playersMap.remove(authToken);
    }
}
