package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnterLobbyRequest {
    private String authToken;
    private String lobbyId;
    
    public EnterLobbyRequest() {
    }
    
    public EnterLobbyRequest(String authToken, String lobbyId) {
        this.authToken = authToken;
        this.lobbyId = lobbyId;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
}
