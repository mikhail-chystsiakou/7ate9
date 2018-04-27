package com.yatty.sevennine.backend.model;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

public class User {
    private String login;
    private String generatedLogin;
    private String passwordHash;
    private int rating;
    
    public static final int INITIAL_RATING = 1600;
    public static final String SALT_DELIMITER = "|";
    
    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        if (login.contains(SALT_DELIMITER)) throw new IllegalArgumentException(
                "Login can not have '" + SALT_DELIMITER + "' sequence"
        );
        this.login = login;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setGeneratedLogin(String generatedLogin) {
        this.generatedLogin = generatedLogin;
    }
    
    public String getGeneratedLogin() {
        return generatedLogin;
    }
    
    /**
     * Generates random 5-digit code, prefixed with '|' sign.
     *
     * @return  random salt to be added to login.
     */
    public static String generateLoginSalt() {
        Random random = new Random(System.currentTimeMillis());
        return SALT_DELIMITER + String.valueOf(Math.abs((random.nextInt() + 10000) % 100000));
    }
    public static String removeLoginSalt(@Nonnull String generatedLogin) {
        return generatedLogin.substring(0, generatedLogin.indexOf(SALT_DELIMITER));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) &&
                Objects.equals(passwordHash, user.passwordHash) &&
                Objects.equals(rating, user.rating);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(login, passwordHash, rating);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", generatedLogin='" + generatedLogin + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", rating=" + rating +
                '}';
    }
}