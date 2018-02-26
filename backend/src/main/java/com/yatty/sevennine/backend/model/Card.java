package com.yatty.sevennine.backend.model;

/**
 * Represents card model.
 *
 * @version 024/02/18
 * @author Dmitry
 */
public class Card {
    private int value;
    private int modifier;

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

    @Override
    public String toString() {
        return value + "±" + modifier;
    }
}
