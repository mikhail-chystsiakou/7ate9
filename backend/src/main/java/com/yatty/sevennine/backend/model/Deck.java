package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.backend.exceptions.logic.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents deck of cards model.
 *
 * @version 14/03/18
 * @author Dmitry
 * @author mike
 */
public class Deck {
    private static final int DECK_SIZE = 73;
    private ArrayList<Card> deck;
    private Card startCard;
    private int cardsPerPlayer;
    private int lastIndex;

    private static final List<Card> DECK_MODEL = Collections.unmodifiableList(new ArrayList<Card>(DECK_SIZE) {{
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 4; j++) {
                add(new Card(i, j));
                add(new Card(i, j));
            }
            if (i < 5)
                add(new Card(i, 1));    // GREEN cards
            if (i > 4 && i < 9)
                add(new Card(i, 2));    // BLUE cards
            if (i < 4 || i > 8)
                add(new Card(i, 3));    // RED cards
        }
    }});

    public Deck(int pn, boolean test) {
        deck = new ArrayList<>(DECK_MODEL);
        Collections.shuffle(deck);
        
        startCard = deck.get(0);
        deck.remove(0);
        
        cardsPerPlayer = (test) ? 3 : (DECK_SIZE - 1) / pn;
    }

    public List<Card> pullCards() {
        System.out.println("Cards per player: " + cardsPerPlayer);
        System.out.println("Deck left size: " + (deck.size() - lastIndex));
        if (deck.size() - lastIndex >= cardsPerPlayer) {
            List<Card> playerList = new ArrayList<>();
            for (int i = lastIndex; i < lastIndex + cardsPerPlayer; i++) {
                playerList.add(deck.get(i));
            }
            lastIndex += cardsPerPlayer;
            return playerList;
        } else {
            throw new EmptyDeckException();
        }
    }
    
    public Card getStartCard() {
        return startCard;
    }
    
    public int getSize() {
        return deck.size();
    }
}
