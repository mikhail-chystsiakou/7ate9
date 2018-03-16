package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnterLobbyResponse {
    private PrivateLobbyInfo privateLobbyInfo;
    private String lobbyId;
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
    
    public PrivateLobbyInfo getPrivateLobbyInfo() {
        return privateLobbyInfo;
    }
    
    public void setPrivateLobbyInfo(PrivateLobbyInfo privateLobbyInfo) {
        this.privateLobbyInfo = privateLobbyInfo;
    }
}
