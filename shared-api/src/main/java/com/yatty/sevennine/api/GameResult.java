package com.yatty.sevennine.api;

import java.util.List;

public class GameResult {
    private String winner;
    private List<PlayerResult> scores;

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<PlayerResult> getScores() {
        return scores;
    }

    public void setScores(List<PlayerResult> scores) {
        this.scores = scores;
    }
}
