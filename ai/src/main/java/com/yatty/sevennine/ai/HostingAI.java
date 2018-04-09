package com.yatty.sevennine.ai;

import com.yatty.sevennine.client.SynchronousClient;

public class HostingAI extends AI {
    private int players;
    
    public HostingAI(SynchronousClient client, Difficulty difficulty, int games, int players) {
        super(client, difficulty, games);
        this.players = players;
    }
    
    @Override
    protected void findGame() {
        // host game
    }
}
