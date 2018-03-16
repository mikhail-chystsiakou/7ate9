package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LobbySubscribeRequest {
    private String authToken;
    
    public LobbySubscribeRequest() {
    }
    
    public LobbySubscribeRequest(String authToken) {
        this.authToken = authToken;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
