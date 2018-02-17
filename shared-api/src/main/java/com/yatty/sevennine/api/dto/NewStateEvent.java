package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewStateEvent {
    public static final String TYPE = "NewStateEvent";
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

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }
}
