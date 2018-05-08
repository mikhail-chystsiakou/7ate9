package com.yatty.sevennine.backend.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * ELO Rating calculator
 *
 * @author radone@gmail.com
 * @author zoxal
 */
public class EloRating {
    private static final int K_FACTOR = 32;
    /**
     * Calculate ELO rating for multiplayer
     *
     * Formula used to calculate rating for Player1
     * NewRatingP1 = RatingP1 + K * (S - EP1)
     *
     * Where:
     * RatingP1 = current rating for Player1
     * K = K-factor (K*EP_n win, -K*EP_n lose)
     * EP_n = Q_n / sum{Q_i}
     * Q_i = 10 ^ (RatingP(i)/400)
     */
    public static void updateUsersRatings(List<LoginedUser> users, @Nonnull LoginedUser winner) {
        if (users.size() == 0) {
            return;
        }
        double sumQ = 0.0;
        for (LoginedUser u : users) sumQ += Math.pow(10.0, (u.getUser().getRating()/400.0));
        for (LoginedUser u : users) {
            int rating = u.getUser().getRating();
            double EP_n = Math.pow(10.0, (rating/400.0)) / sumQ;
            if (winner.equals(u)) {
                u.getUser().setRating((int) Math.round(rating + K_FACTOR * (1 - EP_n)));
            } else {
                u.getUser().setRating((int) Math.round(rating + K_FACTOR * (-EP_n)));
            }
        }
    }
}