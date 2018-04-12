package com.yatty.sevennine.api;

import java.util.Objects;
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
    
    public boolean acceptNext(Card card) {
        int greaterVariant = this.getValue() + this.getModifier();
        if (greaterVariant > 10) greaterVariant -= 10;
        int lesserVariant = this.getValue() - this.getModifier();
        if (lesserVariant <= 0) lesserVariant += 10;
    
        return (card.getValue() == greaterVariant )
                || (card.getValue() == lesserVariant);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value &&
                modifier == card.modifier;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, modifier);
    }
    
    @Override
    public String toString() {
        return value + "±" + modifier;
    }
}
