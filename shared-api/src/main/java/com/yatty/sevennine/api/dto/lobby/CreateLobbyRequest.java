package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLobbyRequest {
    private String lobbyName;
    private int maxPlayersNumber;
    private String authToken;
    
    public CreateLobbyRequest() {
    
    }
    
    public CreateLobbyRequest(String lobbyName, int maxPlayersNumber, String authToken) {
        this.lobbyName = lobbyName;
        this.maxPlayersNumber = maxPlayersNumber;
        this.authToken = authToken;
    }
    
    public String getLobbyName() {
        return lobbyName;
    }
    
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
    
    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }
    
    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
