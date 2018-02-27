package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yatty.sevennine.api.Card;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStartedEvent {
    public static final String TYPE = "GameStartedEvent";
    private Card firstCard;
    private List<Card> playerCards;

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<Card> playerCards) {
        this.playerCards = playerCards;
    }

    public Card getFirstCard() {
        return firstCard;
    }

    public void setFirstCard(Card firstCard) {
        this.firstCard = firstCard;
    }

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "GameStartedEvent{" +
                "firstCard=" + firstCard +
                ", playerCards=" + playerCards +
                '}';
    }
}
