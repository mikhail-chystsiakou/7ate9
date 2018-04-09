package com.yatty.sevennine.ai;

import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.client.SynchronousClient;

public class AI {
    protected SynchronousClient client;
    protected Difficulty difficulty;
    protected int games;
    protected String name;
    
    protected String authToken;
    
    public AI(SynchronousClient client, Difficulty difficulty, int games) {
        this.client = client;
        this.difficulty = difficulty;
        this.games = games;
        
        name = "AI-" + difficulty.getName();
        
        configureClient();
    }
    
    public void run() {
        login();
        while (games > 0) {
            findGame();
            playGame();
        }
        logout();
    }
    
    protected void login() {
        LogInRequest request = new LogInRequest(name);
        LogInResponse response = client.sendMessage(request, LogInResponse.class, false);
        authToken = response.getAuthToken();
    }
    
    /**
     * AI must find the game, enter and wait until game is started
     */
    protected void findGame() {
        // subscribe
        // enter
        // wait for entering
        // wait for start
        // set cards
    }
    
    protected void playGame() {
        // send moves
        // exit on game finish
    }
    
    protected void logout() {
        client.sendMessage(new LogOutRequest(authToken), false);
    }
    
    protected void configureClient() {
//        client.addMessageHandler();
    }
}
