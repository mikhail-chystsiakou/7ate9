package com.yatty.sevennine.backend.model;

import java.net.InetSocketAddress;

/**
 * Represents user in game.
 *
 * @author Mike
 * @version 17/02/18
 */
public class Player {
    private String name;
    private int score;
    private Game game;
    private InetSocketAddress remoteAddress;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void incScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", game=" + game +
                ", remoteAddress=" + remoteAddress +
                '}';
    }
}