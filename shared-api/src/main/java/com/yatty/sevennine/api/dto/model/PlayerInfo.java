package com.yatty.sevennine.api.dto.model;

public class PlayerInfo {
    private String playerId;
    private int rating;
    
    public PlayerInfo() {
    
    }
    
    public PlayerInfo(String playerId, int rating) {
        this.playerId = playerId;
        this.rating = rating;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
}
