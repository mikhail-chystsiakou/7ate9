package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.GameResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewStateEvent {
    public static final String TYPE = "NewStateEvent";
    private int moveNumber;
    private int move;
    private String moveWinner;
    private boolean lastMove;
    private int nextCard;
    private GameResult gameResult;

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

    public String getMoveWinner() {
        return moveWinner;
    }

    public void setMoveWinner(String moveWinner) {
        this.moveWinner = moveWinner;
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

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }
}
