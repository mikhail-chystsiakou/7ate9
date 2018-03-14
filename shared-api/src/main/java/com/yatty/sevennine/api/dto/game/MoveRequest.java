package com.yatty.sevennine.api.dto.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveRequest {
    private int moveNumber;
    private Card move;
    private String gameId;
    private String authToken;

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public Card getMove() {
        return move;
    }

    public void setMove(Card move) {
        this.move = move;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
