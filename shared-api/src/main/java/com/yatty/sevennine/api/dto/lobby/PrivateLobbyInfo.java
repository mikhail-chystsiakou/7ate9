package com.yatty.sevennine.api.dto.lobby;

import com.yatty.sevennine.api.dto.model.PlayerInfo;

import java.util.List;

public class PrivateLobbyInfo {
    private List<PlayerInfo> players;
    
    public List<PlayerInfo> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }
}
