package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class NewStateEvent {
    private int moveNumber;
    private int move;
    private String player;
    private boolean lastMove;
    private int nextCard;

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public boolean isLastMove() {
        return lastMove;
    }

    public void setLastMove(boolean lastMove) {
        this.lastMove = lastMove;
    }

    public int getNextCard() {
        return nextCard;
    }

    public void setNextCard(int nextCard) {
        this.nextCard = nextCard;
    }
}
