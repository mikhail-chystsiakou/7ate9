package com.yatty.sevennine.backend.util;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.GameResult;
import com.yatty.sevennine.api.dto.NewStateEvent;
import com.yatty.sevennine.backend.handlers.PlayerMessageSender;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.GameRegistry;
import com.yatty.sevennine.backend.model.Player;
import io.netty.channel.Channel;

import java.util.List;

/**
 * Sets new random top card every 5 seconds.
 * TODO: DELETE THIS TERRIBLE CODE LATER
 *
 * @author Mike
 * @version 28.02.18
 */
public class CardRotator {
    private static final long SLEEP_TIME = 6000;
    private static volatile long lastRefreshed;
    private static volatile boolean working = false;
    private static final Object locker = new Object();

    public static void start(Channel channel, List<Player> players) {
        lastRefreshed = System.currentTimeMillis();
        working = true;
        new Thread(() -> {
            try {
                while (true) {
                    long timeToSleep;
                    synchronized (locker) {
                        timeToSleep = (lastRefreshed + SLEEP_TIME) - System.currentTimeMillis();
                    }

                    Thread.sleep(timeToSleep);
                    if (!working) return;

                    if ((lastRefreshed + SLEEP_TIME) <= System.currentTimeMillis()) {
                        NewStateEvent newStateEvent = new NewStateEvent();
                        newStateEvent.setNextCard(Card.getRandomCard());
                        Game game = GameRegistry.getFirstGame();
                        game.setTopCard(newStateEvent.getNextCard());
                        PlayerMessageSender.broadcast(channel, players, newStateEvent);
                        synchronized (locker) {
                            lastRefreshed = System.currentTimeMillis();
                        }
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void refresh() {
        synchronized (locker) {
            lastRefreshed = System.currentTimeMillis();
        }
    }

    public static synchronized void stop() {
        synchronized (locker) {
            working = false;
        }
    }
}
