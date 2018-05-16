package com.yatty.sevennine.api;

public class PlayerResult {
    private String playerId;
    private int cardsLeft;
    private int newRating;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

    public void setCardsLeft(int cardsLeft) {
        this.cardsLeft = cardsLeft;
    }
    
    public int getNewRating() {
        return newRating;
    }
    
    public void setNewRating(int newRating) {
        this.newRating = newRating;
    }
}
