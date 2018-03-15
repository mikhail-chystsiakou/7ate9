package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnterLobbyRequest {
    private String authToken;
    private PublicLobbyInfo publicLobbyInfo;
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public PublicLobbyInfo getPublicLobbyInfo() {
        return publicLobbyInfo;
    }
    
    public void setPublicLobbyInfo(PublicLobbyInfo publicLobbyInfo) {
        this.publicLobbyInfo = publicLobbyInfo;
    }
}
