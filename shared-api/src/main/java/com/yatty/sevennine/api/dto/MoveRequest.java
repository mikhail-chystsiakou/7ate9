package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveRequest {
    public static final String TYPE = "MoveRequest";
    private int moveNumber;
    private Card move;
    private String gameId;

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

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "MoveRequest{" +
                "moveNumber=" + moveNumber +
                ", move=" + move +
                ", gameId='" + gameId + '\'' +
                '}';
    }
}
