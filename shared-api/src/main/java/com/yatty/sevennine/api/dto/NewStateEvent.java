package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.GameResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewStateEvent {
    public static final String TYPE = "NewStateEvent";
    private int moveNumber;
    private String moveWinner;
    private boolean lastMove;
    private Card nextCard;
    private GameResult gameResult;

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
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

    public Card getNextCard() {
        return nextCard;
    }

    public void setNextCard(Card nextCard) {
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

    @Override
    public String toString() {
        return "NewStateEvent{" +
                "moveNumber=" + moveNumber +
                ", moveWinner='" + moveWinner + '\'' +
                ", lastMove=" + lastMove +
                ", nextCard=" + nextCard +
                ", gameResult=" + gameResult +
                '}';
    }
}
