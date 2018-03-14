package com.yatty.sevennine.api.dto.lobby;

public class PublicLobbyInfo {
    private String lobbyId;
    private String lobbyName;
    private int maxPlayersNum;
    private int currentPlayersNum;
    
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
    
    public int getMaxPlayersNum() {
        return maxPlayersNum;
    }
    
    public void setMaxPlayersNum(int maxPlayersNum) {
        this.maxPlayersNum = maxPlayersNum;
    }
    
    public int getCurrentPlayersNum() {
        return currentPlayersNum;
    }
    
    public void setCurrentPlayersNum(int currentPlayersNum) {
        this.currentPlayersNum = currentPlayersNum;
    }
}
