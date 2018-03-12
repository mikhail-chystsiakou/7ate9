package com.yatty.sevennine.backend.model;

import io.netty.channel.Channel;

import java.net.SocketAddress;


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
    private SocketAddress remoteAddress;    // used to send notifications
    private Channel socketChannel;              // used during game

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

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void incScore() {
        score++;
    }

    public int getScore() {
        return score;
    }
    
    public Channel getSocketChannel() {
        return socketChannel;
    }
    
    public void setSocketChannel(Channel socketChannel) {
        this.socketChannel = socketChannel;
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