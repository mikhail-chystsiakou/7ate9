package com.yatty.sevennine.backend.model;

import io.netty.channel.Channel;

import javax.annotation.CheckReturnValue;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents 7ate9 logined user.
 */
public class LoginedUser {
    private String name;
    private String authToken;
    // used to send notifications
    // volatile because channel can be updated during KeepAliveRequest
    private volatile Channel channel;
    
    public LoginedUser(String name) {
        this.name = name;
        this.authToken = UUID.randomUUID().toString();
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthToken() {
        return authToken;
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
        return Objects.equals(name, that.name) &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(channel, that.channel);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, authToken, channel);
    }
}
