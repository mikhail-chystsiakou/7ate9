package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveRejectedResponse {
    public static final String TYPE = "MoveRejectedResponse";
    private Card move;

    public Card getMove() {
        return move;
    }

    public void setMove(Card move) {
        this.move = move;
    }

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "MoveRejectedResponse{" +
                "move=" + move +
                '}';
    }
}
