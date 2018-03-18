package com.yatty.sevennine.api.dto.lobby;

public class PublicLobbyInfo {
    private String lobbyId;
    private String lobbyName;
    private int maxPlayersNumber;
    private int currentPlayersNumber;
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
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
    
    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }
    
    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }
}
