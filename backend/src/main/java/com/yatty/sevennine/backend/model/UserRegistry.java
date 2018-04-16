package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.backend.exceptions.logic.PlayerNotFoundException;
import com.yatty.sevennine.backend.exceptions.security.UnauthorizedAccessException;
import io.netty.util.internal.ConcurrentSet;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private static final Map<String, LoginedUser> usersMap = new ConcurrentHashMap<>();
    private static final Set<LoginedUser> subscribers = new ConcurrentSet<>();
    
    /**
     * Registers player in game
     *
     * @param name      player name
     * @return          user auth token
     */
    public static String registerUser(String name) {
        if (usersMap.containsKey(name)) {
//            throw new
        }
        LoginedUser player = new LoginedUser(name);
        usersMap.put(player.getAuthToken(), player);
        return player.getAuthToken();
    }
    
    @Nullable
    public static LoginedUser getUserByToken(String authToken) {
        return usersMap.get(authToken);
    }
    
    public static void removeUserByToken(String authToken) {
        usersMap.remove(authToken);
    }
    
    /**
     * Checks if user is authorized. Th
     *
     * @param authToken user auth token to check
     * @throws UnauthorizedAccessException  if user is not registered
     */
    public static LoginedUser checkAndGetLoginedUser(String authToken) {
        LoginedUser user = usersMap.get(authToken);
        if (user == null) {
            throw new UnauthorizedAccessException(authToken);
        }
        return user;
    }
    
    public static void addSubscriber(String authToken) {
        if (usersMap.containsKey(authToken)) {
            subscribers.add(usersMap.get(authToken));
        } else {
            throw new PlayerNotFoundException(authToken);
        }
    }
    
    public static void removeSubscriber(String authToken) {
        if (usersMap.containsKey(authToken)) {
            subscribers.remove(usersMap.get(authToken));
        } else {
            throw new PlayerNotFoundException(authToken);
        }
    }
    
    /**
     * @return  Unmodifiable view of subscribers set
     */
    public static Set<LoginedUser> getSubscribers() {
        return Collections.unmodifiableSet(subscribers);
    }
}
