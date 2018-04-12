package com.yatty.sevennine.api.dto.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.GameResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewStateNotification {
    private int moveNumber;
    private String moveWinner;
    private boolean lastMove;
    private boolean stalemate;
    private Card nextCard;
    private GameResult gameResult;
    
    public NewStateNotification() {
    }
    
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
    
    public boolean isStalemate() {
        return stalemate;
    }
    
    public void setStalemate(boolean stalemate) {
        this.stalemate = stalemate;
    }
    
    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }
}
