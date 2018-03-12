package com.yatty.sevennine.backend.model;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRegistry {
    private static final Map<String, Player> playersMap = new ConcurrentHashMap<>();

    public static void registerPlayer(String name, SocketAddress address) {
        Player player = new Player(name);
        player.setRemoteAddress(address);
        playersMap.put(name, player);
    }
    
    public static Player getPlayer(String playerName) {
        return playersMap.get(playerName);
    }
    
    public static boolean checkPlayerRegistered(String playerName) {
        return playersMap.containsKey(playerName);
    }
    
    public static void removePlayerByName(String playerName) {
        playersMap.remove(playerName);
    }
}
