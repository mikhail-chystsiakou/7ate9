package com.yatty.sevennine.backend.exceptions.logic;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class LobbyNotFoundException extends SevenNineException {
    public LobbyNotFoundException(String lobbyId) {
        super("Lobby or game with id'" + lobbyId + "' not found");
    }
    
    public LobbyNotFoundException(String description, String lobbyId) {
        super(description + "Lobby or game with id'" + lobbyId + "' not found");
    }
}
