package com.yatty.sevennine.ai;

import com.yatty.sevennine.api.dto.lobby.CreateLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.CreateLobbyResponse;
import com.yatty.sevennine.client.ServerException;
import com.yatty.sevennine.client.SynchronousClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostingAI extends AI {
    public static final Logger logger = LoggerFactory.getLogger(HostingAI.class);
    private int players;
    
    public HostingAI(SynchronousClient client, Difficulty difficulty, int games, int players) {
        super(client, difficulty, games);
        this.players = players;
    }
    
    @Override
    protected void enterLobby() {
        CreateLobbyRequest request = new CreateLobbyRequest();
        request.setAuthToken(authToken);
        request.setMaxPlayersNumber(players);
        request.setLobbyName(name);
    
        try {
            CreateLobbyResponse response = client.sendMessage(request, CreateLobbyResponse.class, true);
            gameId = response.getLobbyId();
            logger.debug("Entered {}", gameId);
        } catch (ServerException e) {
            logger.error("Failed to create lobby", e);
            throw new RuntimeException("Failed to create lobby", e);
        }
    }
}
