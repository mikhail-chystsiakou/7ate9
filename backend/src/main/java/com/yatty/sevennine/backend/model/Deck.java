package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents deck of cards model.
 *
 * @version 024/02/18
 * @author Dmitry
 */
public class Deck {
    private ArrayList<Card> cardList;
    private Random rnd;
    private int playersNumber;  // от 2 до 4 игроков

    private static final List<Card> DECK_LIST = Collections.unmodifiableList(new ArrayList<Card>() {{
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 4; j++) {
                add(new Card(i, j));
                add(new Card(i, j));
            }
            if (i < 5)
                add(new Card(i, 1));    // GREEN cards adding
            if (i > 4 && i < 9)
                add(new Card(i, 2));    // BLUE cards
            if (i < 4 || i > 8)
                add(new Card(i, 3));    // RED cards
        }
    }});

    public Deck(int pn) {
        playersNumber = (pn > 1 && pn < 5) ? pn : 4;    // TODO: определить дефолтное значение количества игроков
        rnd = new Random(System.currentTimeMillis());
        cardList = new ArrayList<>(DECK_LIST);
    }

    private Deck(ArrayList<Card> list) {
        cardList = (list != null) ? new ArrayList<>(list) : null;
    }

    public void shuffle() {
        if (cardList!=null && !cardList.isEmpty())
            Collections.shuffle(cardList, rnd);
    }

    public ArrayList<Card> pullCards() {
        if (!cardList.isEmpty()) {
            ArrayList<Card> playerList = new ArrayList<>();
            for (int i = 0; i < DECK_LIST.size() / playersNumber; i++) {
                playerList.add(cardList.remove(0));
            }
            return playerList;
        } else {
            return null;
        }
    }

    public Deck pullCards(int asDeck) {
        return new Deck(pullCards());
    }

    public Card getStartCard() {
        if (!cardList.isEmpty() && cardList.size()%2 != 0)
            return cardList.remove(rnd.nextInt(cardList.size()));
        else
            return null;
    }

    public int getSize() {
        return cardList.size();
    }

    public String toString() {
        String string = "";
        if (cardList != null)
            for (int i = 0; i < cardList.size(); i++)
                string += cardList.get(i) + "; ";
        return string;
    }
}
