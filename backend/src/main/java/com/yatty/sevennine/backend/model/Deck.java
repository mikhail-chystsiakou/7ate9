package com.yatty.sevennine.backend.model;

import java.util.ArrayList;
import java.util.Collections;
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
    private int playersNumber;      // от 2 до 4 игроков

    private static final int DEFAULT_CARDS_AMOUNT = 18;     //  в полной колоде 73 карты, т.е. делим на максимальное число игроков(4) и получаем 18 карт на одного игрока

    public Deck() {
        playersNumber = 0;
        rnd = new Random(System.currentTimeMillis());
        cardList = new ArrayList<>();
    }

    private Deck(ArrayList<Card> list) {
        cardList = (list != null) ? new ArrayList<>(list) : null;
    }

    public void generate(int pn) {
        if (playersNumber == 0) {
            playersNumber = (pn > 1 && pn < 5) ? pn : 2;    // TODO: определить дефолтное значение количества игроков

            Card.Color[] colors = Card.Color.values();
            for (int i = 0; i < playersNumber * DEFAULT_CARDS_AMOUNT + 1; i++) {
                cardList.add(new Card(rnd.nextInt(10) + 1, rnd.nextInt(3) + 1, colors[rnd.nextInt(3)]));
            }
        }
    }

    public void shuffle() {
        if (cardList!=null && !cardList.isEmpty())
        for (int i = 0; i < playersNumber * DEFAULT_CARDS_AMOUNT; i++)
            Collections.swap(cardList, rnd.nextInt(cardList.size()), rnd.nextInt(cardList.size()));
    }

    public ArrayList<Card> pullCards() {
        if (cardList != null && !cardList.isEmpty()) {
            ArrayList<Card> playerList = new ArrayList<>();
            for (int i = 0; i < DEFAULT_CARDS_AMOUNT; i++) {
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
        if (cardList!=null && !cardList.isEmpty() && cardList.size()%2 != 0)
            return cardList.remove(rnd.nextInt(cardList.size()));
        else
            return null;
    }

    public int getSize() {
        return cardList.size();
    }

    public String toString() {
        String string = "";
        if (cardList != null) {
            for (int i = 0; i < cardList.size(); i++)
                string += cardList.get(i) + "; ";
            return string;
        }
        return null;
    }
}
