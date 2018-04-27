package com.yatty.sevennine.backend.model;

import io.netty.channel.Channel;

import javax.annotation.CheckReturnValue;
import java.util.Objects;

/**
 * Represents 7ate9 logined user.
 */
public class LoginedUser {
    private User user;
    private String authToken;
    // used to send notifications
    // volatile because channel can be updated during KeepAliveRequest
    private volatile Channel channel;
    
    public LoginedUser() {
    
    }
    
    public LoginedUser(User user) {
        this.user = user;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    
    @CheckReturnValue
    public Channel getChannel() {
        return channel;
    }
    
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginedUser that = (LoginedUser) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(channel, that.channel);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, channel);
    }
}
