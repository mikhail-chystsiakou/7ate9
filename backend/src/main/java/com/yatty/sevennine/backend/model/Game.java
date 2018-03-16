package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.api.Card;
import com.yatty.sevennine.api.PlayerResult;
import com.yatty.sevennine.api.dto.lobby.PrivateLobbyInfo;
import com.yatty.sevennine.api.dto.lobby.PublicLobbyInfo;
import com.yatty.sevennine.api.dto.model.PlayerInfo;
import com.yatty.sevennine.backend.exceptions.logic.FullLobbyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    
    private List<Player> players;
    private int expectedPlayersNum;
    private Player winner;
    
    private Card topCard;
    private int moveNumber;

    public Game(String name, int expectedPlayersNum) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.expectedPlayersNum = expectedPlayersNum;
        this.players = new ArrayList<>(MAX_PLAYERS_NUM);
    }

    /**
     * Player is joining the game.
     *
     * @param   user                user that wants to join the game
     */
    public synchronized void addPlayer(LoginedUser user) {
        if (players.size() >= expectedPlayersNum) throw new FullLobbyException(this);
        Player player = new Player(user);
        players.add(player);
    }
    
    /**
     * Checks, that user has already joined this game.
     *
     * @param user                  user to check
     */
    public boolean checkUserJoined(LoginedUser user) {
        for (Player p : players) {
            if (p.getLoginedUser().equals(user)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Initializes player cards and top card
     */
    public void giveOutCards() {
        Deck deck = new Deck(expectedPlayersNum);
        topCard = deck.getStartCard();
        players.forEach(p -> p.setCards(deck.pullCards()));
    }
    
    public boolean acceptMove(Card move, LoginedUser moveAuthor) {
        logger.debug("Accepting move {} for topCard {}", move, topCard);

        int greaterVariant = topCard.getValue() + topCard.getModifier();
        if (greaterVariant > 10) greaterVariant -= 10;
        int lesserVariant = topCard.getValue() - topCard.getModifier();
        if (lesserVariant <= 0) lesserVariant += 10;

        boolean validMove = (move.getValue() == greaterVariant ) || (move.getValue() == lesserVariant);

        if (validMove) {
            for (Player p : players) {
                if (p.getLoginedUser().equals(moveAuthor)) {
                    logger.debug("NOW PLAYER HAS {} CARDS", p.getCards().size());
                    setTopCard(move);
                    moveNumber++;
                    p.cards.remove(move);
                    if (p.getCards().size() <= 1) {
                        winner = p;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setTopCard(Card topCard) {
        logger.trace("New top card '{}' in the game '{}'", topCard, this.id);
        this.topCard = topCard;
    }
    
    /**
     * @return  unmodifiable view of game players as logined users
     */
    public List<LoginedUser> getLoginedUsers() {
        return Collections.unmodifiableList(
            players.stream()
                .map(Player::getLoginedUser)
                .collect(Collectors.toList()
                )
        );
    }
    
    /**
     * @return  unmodifiable view of game players
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
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

    public boolean isFull() {
        return players.size() >= expectedPlayersNum;
    }
    
    public boolean isFinished() {
        return winner != null;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public Card getTopCard() {
        return topCard;
    }
    
    public PublicLobbyInfo getPublicLobbyInfo() {
        PublicLobbyInfo publicLobbyInfo = new PublicLobbyInfo();
        publicLobbyInfo.setLobbyId(this.id);
        publicLobbyInfo.setLobbyName(this.name);
        publicLobbyInfo.setMaxPlayersNum(this.expectedPlayersNum);
        publicLobbyInfo.setCurrentPlayersNum(this.getPlayers().size());
        return publicLobbyInfo;
    }
    
    public PrivateLobbyInfo getPrivateLobbyInfo() {
        PrivateLobbyInfo privateLobbyInfo = new PrivateLobbyInfo();
        privateLobbyInfo.setPlayers(players
                .stream()
                .map(p -> new PlayerInfo(p.getLoginedUser().getName()))
                .collect(Collectors.toList())
        );
        return privateLobbyInfo;
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
        
        public PlayerResult getResult() {
            PlayerResult playerResult = new PlayerResult();
            playerResult.setPlayerName(loginedUser.getName());
            playerResult.setCardsLeft(cards.size());
            return playerResult;
        }
    }
}