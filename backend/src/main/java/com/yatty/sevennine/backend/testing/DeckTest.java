package com.yatty.sevennine.backend.testing;

import com.yatty.sevennine.backend.model.Deck;

public class DeckTest {
    public static void main(String args[]) {
        for (int i = 1; i < 5; i++) {
            Deck deck = new Deck(i);
            System.out.println("Generated deck: " + deck);
            System.out.println("Deck size: " + deck.getSize());
            // shuffle test
            System.out.println("Shuffled deck: " + deck);
            System.out.println("Deck size: " + deck.getSize());
            // start card pulling test
            System.out.println("Start card: " + deck.getStartCard());
            System.out.println("Start card: " + deck.getStartCard());
            System.out.println("Deck size: " + deck.getSize());
            // player cards pulling test
            System.out.println("First pull: " + deck.pullCards());
            System.out.println("Deck size: " + deck.getSize());
            System.out.println("Second pull: " + deck.pullCards());
            System.out.println("Deck size: " + deck.getSize());
            System.out.println("Third pull: " + deck.pullCards());
            System.out.println("Deck size: " + deck.getSize());
        }
    }
}
