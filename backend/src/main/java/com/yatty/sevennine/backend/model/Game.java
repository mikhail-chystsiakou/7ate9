package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.PlayerResult;
import com.yatty.sevennine.api.dto.lobby.PrivateLobbyInfo;
import com.yatty.sevennine.api.dto.lobby.PublicLobbyInfo;
import com.yatty.sevennine.api.dto.model.PlayerInfo;
import com.yatty.sevennine.backend.exceptions.logic.FullLobbyException;
import com.yatty.sevennine.backend.exceptions.security.GameAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the stateful logic/game object.
 *
 * @version 26/02/18
 * @author Mike
 */
public class Game {
    public static final Logger logger = LoggerFactory.getLogger(Game.class);
    private static final int MAX_PLAYERS_NUM = 4;

    private String id;
    private String name;
    
    private List<Player> currentPlayers = new ArrayList<>(MAX_PLAYERS_NUM);
    private List<LoginedUser> registeredPlayers = new ArrayList<>(MAX_PLAYERS_NUM);
    private int expectedPlayersNum;
    private Player winner;
    
    private Card topCard;
    private int moveNumber;
    private boolean started;
    
    private Deck deck;

    public Game(String name, int expectedPlayersNum) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.expectedPlayersNum = expectedPlayersNum;
    }

    /**
     * Player is joining the game.
     *
     * @param   user                user that wants to join the game
     */
    public void register(LoginedUser user) {
        if (started) throw new IllegalStateException("Game already started");
        if (registeredPlayers.size() >= expectedPlayersNum) throw new FullLobbyException(this);
        registeredPlayers.add(user);
    }
    
    public void unregisterPlayer(LoginedUser user) {
        if (started) throw new IllegalStateException("Game already started");
        checkUserJoined(user);
        for (LoginedUser u: registeredPlayers) {
            if (u.equals(user)) {
                currentPlayers.remove(u);
            }
        }
    }
    
    public void playerLeave(LoginedUser user) {
        checkUserJoined(user);
        for (Player p : currentPlayers) {
            if (p.getLoginedUser().equals(user)) {
                currentPlayers.remove(p);
            }
        }
        if (currentPlayers.size() == 1) {
            winner = currentPlayers.get(0);
        }
    }
    
    public void start() {
        started = true;
        for (LoginedUser u : registeredPlayers) currentPlayers.add(new Player(u));
        giveOutCards();
    }
    
    /**
     * Initializes player cards and top card
     */
    private void giveOutCards() {
        if (deck == null) {
            deck = new SevenAteNineDeck(expectedPlayersNum, false);
        }
        topCard = deck.pullStartCard();
        currentPlayers.forEach(p -> p.setCards(deck.pullCards()));
    }
    
    /**
     * Validates move and updates state if move is right.
     *
     * @param move                  move to
     * @param moveAuthor            author of move
     * @return true                 if move has been accepted
     * @throws GameAccessException  if player was not found in the game
     */
    public boolean acceptMove(@Nonnull Card move, @Nonnull LoginedUser moveAuthor) {
        checkUserJoined(moveAuthor);
        
        logger.trace("Accepting move {} for topCard {}", move, topCard);
        
        if (!topCard.acceptNext(move)) {
            logger.trace("Rejecting move {} for topCard {}", move, topCard);
            return false;
        }

        Player moveWinner = currentPlayers.stream()
                .filter(p -> moveAuthor.equals(p.getLoginedUser()))
                .findFirst()
                .orElseThrow(() -> new GameAccessException(moveAuthor, this));
        
        setTopCard(move);
        moveNumber++;
        moveWinner.removeCard(move);
        if (moveWinner.getCards().size() <= 1) {
            winner = moveWinner;
        }
        return true;
    }
    
    public void setTopCard(Card topCard) {
        logger.trace("New top card '{}' in the game '{}'", topCard, this.id);
        this.topCard = topCard;
    }
    
    /**
     * @return  unmodifiable view of game currentPlayers as logined users
     */
    public List<LoginedUser> getCurrentLoginedUsers() {
        return Collections.unmodifiableList(
            currentPlayers.stream()
                .map(Player::getLoginedUser)
                .collect(Collectors.toList()
                )
        );
    }
    
    /**
     * @return  unmodifiable view of game currentPlayers
     */
    public List<LoginedUser> getRegisteredPlayers() {
        return Collections.unmodifiableList(registeredPlayers);
    }
    
    /**
     * @return  unmodifiable view of game currentPlayers
     */
    public List<Player> getCurrentPlayers() {
        return Collections.unmodifiableList(currentPlayers);
    }
    
    /**
     * Checks if at least one player has legal move.
     *
     * @return  true    if no one has move to do
     */
    public boolean isStalemate() {
        for (Player p : currentPlayers) {
            if (p.getCards().stream().limit(Player.VISIABLE_CARDS).filter(topCard::acceptNext).count() > 0) {
                return false;
            }
        }
        return true;
    }
    
    public void fixStalemate() {
        // TODO: add logic here
        while (isStalemate()) {
            topCard = Card.getRandomCard();
        }
        moveNumber++;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getExpectedPlayersNum() {
        return expectedPlayersNum;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
    
    public void incMoveNumber() {
        moveNumber++;
    }

    public boolean isFull() {
        return registeredPlayers.size() >= expectedPlayersNum;
    }
    
    public boolean isFinished() {
        return winner != null || currentPlayers.size() == 0;
    }
    
    @Nullable
    public Player getWinner() {
        return winner;
    }
    
    public Card getTopCard() {
        return topCard;
    }
    
    public void setDeck(Deck deck) {
        this.deck = deck;
    }
    
    public PublicLobbyInfo getPublicLobbyInfo() {
        PublicLobbyInfo publicLobbyInfo = new PublicLobbyInfo();
        publicLobbyInfo.setLobbyId(this.id);
        publicLobbyInfo.setLobbyName(this.name);
        publicLobbyInfo.setMaxPlayersNumber(this.expectedPlayersNum);
        publicLobbyInfo.setCurrentPlayersNumber(this.getCurrentPlayers().size());
        return publicLobbyInfo;
    }
    
    public PrivateLobbyInfo getPrivateLobbyInfo() {
        PrivateLobbyInfo privateLobbyInfo = new PrivateLobbyInfo();
        privateLobbyInfo.setPlayers(registeredPlayers
                .stream()
                .map(p -> new PlayerInfo(
                        p.getUser().getGeneratedLogin(),
                        p.getUser().getRating())
                )
                .collect(Collectors.toList())
        );
        return privateLobbyInfo;
    }
    
    
    /**
     * Checks, that user has already joined this game.
     *
     * @param user                  user to check
     * @throws GameAccessException  if user has not joined the game
     */
    public void checkUserJoined(LoginedUser user) throws GameAccessException {
        for (Player p : currentPlayers) {
            if (p.getLoginedUser().equals(user)) {
                return;
            }
        }
        throw new GameAccessException(user, this);
    }
    
    /**
     * Represents user, that is playing in game.
     *
     * @author Mike
     * @version 17/02/18
     */
    public class Player {
        private LoginedUser loginedUser;
        private List<Card> cards;
        public static final int VISIABLE_CARDS = 8;
        
        Player(@Nonnull LoginedUser loginedUser) {
            this.loginedUser = loginedUser;
        }
    
        public LoginedUser getLoginedUser() {
            return loginedUser;
        }
    
        public List<Card> getCards() {
            return Collections.unmodifiableList(cards);
        }
    
        void setCards(List<Card> cards) {
            this.cards = cards;
        }
        
        void removeCard(Card card) {
            cards.remove(card);
        }
        
        public PlayerResult getResult() {
            PlayerResult playerResult = new PlayerResult();
            playerResult.setPlayerName(loginedUser.getUser().getGeneratedLogin());
            playerResult.setCardsLeft(cards.size());
            return playerResult;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return Objects.equals(loginedUser, player.loginedUser);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(loginedUser);
        }
    }
}