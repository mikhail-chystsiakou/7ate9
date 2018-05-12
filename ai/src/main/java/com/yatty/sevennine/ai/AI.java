package com.yatty.sevennine.ai;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.GameStartedNotification;
import com.yatty.sevennine.api.dto.game.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.game.MoveRequest;
import com.yatty.sevennine.api.dto.game.NewStateNotification;
import com.yatty.sevennine.api.dto.lobby.*;
import com.yatty.sevennine.client.ServerException;
import com.yatty.sevennine.client.SynchronousClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class AI {
    private static final Logger logger = LoggerFactory.getLogger(AI.class);
    
    protected SynchronousClient client;
    protected Difficulty difficulty;
    protected int games;
    protected String name;
    protected String playerId;
    
    protected volatile String authToken;
    
    protected final Object lobbyListLock = new Object();
    protected volatile Collection<PublicLobbyInfo> lobbyList;
    protected volatile Boolean lobbyListUpdated = false;
    
    protected final Object gameStartedLock = new Object();
    protected volatile Card topCard;
    private volatile List<Card> cards;
    private volatile Card lastMove;
    
    protected final Object gameMoveLock = new Object();
    protected volatile boolean gameUpdated;
    
    protected volatile String gameId;
    
    public AI(SynchronousClient client, Difficulty difficulty, int games) {
        logger.debug("Simple AI started");
        this.client = client;
        this.difficulty = difficulty;
        this.games = games;
        
        name = "AI-" + difficulty.getName();
        
        configureClient();
    }
    
    public void run() {
        login();
        while (games > 0) {
            enterLobby();
            waitForStart();
            playGame();
            games--;
        }
        logout();
    }
    
    protected void login() {
        try {
            logger.debug("Logging in...");
            String passwordHash = new String(
                    MessageDigest.getInstance("SHA-256").digest(UUID.randomUUID().toString().getBytes())
            );
            LogInRequest request = new LogInRequest(name, passwordHash);
            LogInResponse response = client.sendMessage(request, LogInResponse.class, false);
            authToken = response.getAuthToken();
            playerId = response.getPlayerId();
            logger.debug("Logged in as {} ({}): {}", playerId, passwordHash, authToken);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unexpected error", e);
        }
    }
    
    protected void enterLobby() {
        client.sendMessage(new LobbyListSubscribeRequest(authToken), true);
        
        while (gameId == null) {
            synchronized (lobbyListLock) {
                try {
                    while (!lobbyListUpdated) {
                        lobbyListLock.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted during waiting for lobby list updates", e);
                }
            }
            lobbyListUpdated = false;
            
            Iterator<PublicLobbyInfo> lobbyInfo;
            synchronized (lobbyListLock) {
                lobbyInfo = lobbyList.iterator();
            }
            
            while (lobbyInfo.hasNext() && gameId == null) {
                String lobbyToTry = lobbyInfo.next().getLobbyId();
                if (lobbyListUpdated) break;
    
                try {
                    client.sendMessage(new EnterLobbyRequest(authToken, lobbyToTry), EnterLobbyResponse.class);
                    gameId = lobbyToTry;
                    client.sendMessage(new LobbyListUnsubscribeRequest(authToken));
                } catch (ServerException e) {
                    System.out.println("Failed to join lobby " + lobbyToTry);
                }
            }
        }
        logger.debug("Entered {}", gameId);
    }
    
    protected void waitForStart() {
        synchronized (gameStartedLock) {
            try {
                while (topCard == null) {
                    gameStartedLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void playGame() {
        do {
            Card move = findMove();
            if (move != null) {
                if (gameUpdated) {
                    gameUpdated = false;
                    continue;
                }
                try {
                    Thread.sleep(difficulty.getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setAuthToken(authToken);
                moveRequest.setGameId(gameId);
                moveRequest.setMove(move);
                
                if (gameUpdated) {
                    gameUpdated = false;
                    continue;
                }
                
                client.sendMessage(moveRequest, true);
                lastMove = move;
                logger.debug("Moved: {}. Cards left: {}", move, cards);
            }
            
            synchronized (gameMoveLock) {
                try {
                    while (!gameUpdated) {
                        gameMoveLock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameUpdated = false;
            }
        } while (topCard != null);
        gameId = null;
    }
    
    protected void logout() {
        client.sendMessage(new LogOutRequest(authToken), false);
    }
    
    protected Card findMove() {
        for (Card c : cards) {
            if (topCard.acceptNext(c)) {
                return c;
            }
        }
        return null;
    }
    
    protected void configureClient() {
        client.addMessageHandler(m -> {
            synchronized (lobbyListLock) {
                lobbyListUpdated = true;
                lobbyList = m.getLobbyList();
                lobbyListLock.notify();
            }
        }, LobbyListUpdatedNotification.class);
    
        client.addMessageHandler(m -> {
            synchronized (gameStartedLock) {
                topCard = m.getFirstCard();
                cards = m.getPlayerCards();
                gameStartedLock.notify();
            }
        }, GameStartedNotification.class);
    
        client.addMessageHandler(m -> {
            synchronized (gameMoveLock) {
                topCard = m.getNextCard();
                logger.debug("New top card: {}", m.getNextCard());
                gameUpdated = true;
                if (playerId.equals(m.getMoveWinner())
                        && m.getNextCard() != null
                        && m.getNextCard().equals(lastMove)) {
                    cards.remove(m.getNextCard());
                }
                lastMove = null;
                gameMoveLock.notify();
            }
        }, NewStateNotification.class);
    
        client.addMessageHandler(m -> {
            synchronized (gameMoveLock) {
                System.out.println("Move rejected: " + m.getMove());
                gameUpdated = true;
                lastMove = null;
                gameMoveLock.notify();
            }
        }, MoveRejectedResponse.class);
    }
}
