package com.yatty.sevennine.backend.junit;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.backend.model.Deck;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.LoginedUser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class GameTest {

    @org.junit.Test
    public void cardRotateTest() {
        LoginedUser loginedUser1 = new LoginedUser("Player1");
        LoginedUser loginedUser2 = new LoginedUser("Player2");

        Game game = new Game("Game", 2);
        game.addPlayer(loginedUser1);
        game.addPlayer(loginedUser2);

        Deck deck = new SimpleDeck();
        game.setDeck(deck);
        game.giveOutCards();
        game.acceptMove(new Card(2, 2), loginedUser1);
        assertEquals(game.isStalemate(), true);
    }

    class SimpleDeck implements Deck {

        @Override
        public Card pullStartCard() {
            return new Card(1, 1);
        }

        @Override
        public List<Card> pullCards() {
            ArrayList<Card> cardsArrayList = new ArrayList<>();
            cardsArrayList = new ArrayList<>();
            cardsArrayList.add(new Card(1, 1));
            return cardsArrayList;
        }
    }
}
