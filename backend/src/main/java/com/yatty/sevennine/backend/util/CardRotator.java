package com.yatty.sevennine.backend.util;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.dto.game.NewStateEvent;
import com.yatty.sevennine.backend.model.Game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Sets new random top card every {@link #SLEEP_TIME} seconds.
 * TODO: use java executors api
 *
 * @author Mike
 * @version 13.03.18
 */
public class CardRotator {
    private static Map<String, CardRotator> rotators = new ConcurrentHashMap<>();
    public static final long SLEEP_TIME = 10000;
    private AtomicLong lastRefreshed = new AtomicLong();
    private final AtomicBoolean working = new AtomicBoolean(false);
    private Game game;
    
    private CardRotator(Game game) {
        lastRefreshed.set(System.currentTimeMillis());
        this.game = game;
    }

    private void start() {
        if (working.get()) {
            throw new IllegalStateException("Rotator is already running");
        }
        working.set(true);
        rotators.put(game.getId(), this);
        new Thread(new RotatorThread()).start();
    }
    
    public static void start(Game game) {
        new CardRotator(game).start();
    }
    
    public static void refresh(String gameId) {
        rotators.get(gameId).lastRefreshed.set(System.currentTimeMillis());
    }

    public static synchronized void stop(String gameId) {
        rotators.get(gameId).working.set(false);
        rotators.remove(gameId);
    }
    
    private class RotatorThread implements Runnable {
        @Override
        public void run() {
            try {
                while (working.get()) {
                    long timeToSleep;
                    timeToSleep = (lastRefreshed.get() + SLEEP_TIME) - System.currentTimeMillis();
            
                    Thread.sleep(timeToSleep);
                    if (!working.get()) return;
            
                    if ((lastRefreshed.get() + SLEEP_TIME) <= System.currentTimeMillis()) {
                        NewStateEvent newStateEvent = new NewStateEvent();
                        newStateEvent.setNextCard(Card.getRandomCard());
                        game.setTopCard(newStateEvent.getNextCard());
                        game.getLoginedUsers().forEach(u -> {
                            u.getChannel().writeAndFlush(newStateEvent);
                        });
                        lastRefreshed.set(System.currentTimeMillis());
                    }
            
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
