package com.yatty.sevennine.api.dto.lobby;

public class CreateLobbyRequest {
    private PublicLobbyInfo publicLobbyInfo;
    private String lobbyName;
    private int maxPlayersNumber;
    private String authToken;
    
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
