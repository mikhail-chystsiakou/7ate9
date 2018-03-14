package com.yatty.sevennine.api.dto.lobby;

import java.util.Collection;

public class LobbyListUpdatedNotification {
    private Collection<PublicLobbyInfo> lobbyList;
    
    public Collection<PublicLobbyInfo> getLobbyList() {
        return lobbyList;
    }
    
    public void setLobbyList(Collection<PublicLobbyInfo> lobbyList) {
        this.lobbyList = lobbyList;
    }
}
