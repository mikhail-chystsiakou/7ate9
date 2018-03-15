package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLobbyResponse {
    private String lobbyId;
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
}
