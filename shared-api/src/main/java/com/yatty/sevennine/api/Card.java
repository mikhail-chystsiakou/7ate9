package com.yatty.sevennine.api;

import java.util.Random;

/**
 * Represents card model.
 *
 * @version 024/02/18
 * @author Dmitry
 */
public class Card {
    private int value;
    private int modifier;

    public Card() {

    }

    public Card(int largeNumber, int smallNumber) {
        value = largeNumber;
        modifier = smallNumber;
    }

    public int getValue() {
        return value;
    }

    public int getModifier() {
        return modifier;
    }

    public static Card getRandomCard() {
        Random random = new Random(System.currentTimeMillis());
        return new Card(random.nextInt(10) + 1, random.nextInt(3) + 1);
    }

    @Override
    public String toString() {
        return value + "±" + modifier;
    }
}
