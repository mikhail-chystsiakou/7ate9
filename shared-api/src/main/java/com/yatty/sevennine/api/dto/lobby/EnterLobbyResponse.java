package com.yatty.sevennine.api.dto.lobby;

public class EnterLobbyResponse {
    private PrivateLobbyInfo privateLobbyInfo;
    
    public PrivateLobbyInfo getPrivateLobbyInfo() {
        return privateLobbyInfo;
    }
    
    public void setPrivateLobbyInfo(PrivateLobbyInfo privateLobbyInfo) {
        this.privateLobbyInfo = privateLobbyInfo;
    }
}
