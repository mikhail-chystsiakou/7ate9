package com.yatty.sevennine.api.dto.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LobbyListUpdatedNotification {
    private Collection<PublicLobbyInfo> lobbyList;
    
    public Collection<PublicLobbyInfo> getLobbyList() {
        return lobbyList;
    }
    
    public void setLobbyList(Collection<PublicLobbyInfo> lobbyList) {
        this.lobbyList = lobbyList;
    }
}
