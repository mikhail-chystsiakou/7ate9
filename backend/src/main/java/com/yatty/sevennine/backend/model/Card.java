package com.yatty.sevennine.backend.model;

/**
 * Represents card model.
 *
 * @version 024/02/18
 * @author Dmitry
 */
public class Card {
    private int largeNumeral;
    private int smallNumeral;
    private Color color;

    public static enum Color {
        RED,
        GREEN,
        BLUE
    }

    public Card(int ln, int sn, Color col) {
        largeNumeral = ln;
        smallNumeral = sn;
        color = col;
    }

    public int getLargeNumeral() {
        return largeNumeral;
    }

    public int getSmallNumeral() {
        return smallNumeral;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return largeNumeral + "±" + smallNumeral + color;
    }
}
