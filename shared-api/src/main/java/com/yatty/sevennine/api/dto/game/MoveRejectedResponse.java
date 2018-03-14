package com.yatty.sevennine.api.dto.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveRejectedResponse {
    private Card move;

    public Card getMove() {
        return move;
    }

    public void setMove(Card move) {
        this.move = move;
    }
}
