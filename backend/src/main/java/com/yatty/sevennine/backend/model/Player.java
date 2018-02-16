package com.yatty.sevennine.backend.model;


import io.netty.channel.Channel;

public class Player {
    private String name;
    private int score;
    private Channel remoteAddress;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Channel getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(Channel remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
