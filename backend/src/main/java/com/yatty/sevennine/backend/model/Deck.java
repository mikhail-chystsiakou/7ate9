package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;

import java.util.List;

public interface Deck {
    Card pullStartCard();
    List<Card> pullCards();
}