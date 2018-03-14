package com.yatty.sevennine.api.dto.lobby;

public class CreateLobbyRequest {
    private PublicLobbyInfo publicLobbyInfo;
    private String authToken;
    
    public PublicLobbyInfo getPublicLobbyInfo() {
        return publicLobbyInfo;
    }
    
    public void setPublicLobbyInfo(PublicLobbyInfo publicLobbyInfo) {
        this.publicLobbyInfo = publicLobbyInfo;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
