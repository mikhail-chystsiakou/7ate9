package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class GameStartedEvent {
    private int card;

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }
}
