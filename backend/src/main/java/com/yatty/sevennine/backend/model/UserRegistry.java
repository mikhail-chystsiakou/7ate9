package com.yatty.sevennine.backend.model;

import com.yatty.sevennine.backend.data.DatabaseDriver;
import com.yatty.sevennine.backend.exceptions.security.LogInException;
import com.yatty.sevennine.backend.exceptions.security.UnauthorizedAccessException;
import io.netty.util.internal.ConcurrentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    public static final Logger logger = LoggerFactory.getLogger(UserRegistry.class);
    private static final Map<String, LoginedUser> usersMap = new ConcurrentHashMap<>();
    private static final Set<LoginedUser> subscribers = new ConcurrentSet<>();
    
    /**
     * If there is user with specified credentials, fetches information
     * about it. If there was no such user, creates new one.
     *
     * @param login             user's login
     * @param passwordHash      user's password hash
     * @return                  logined user with auth token
     * @throws LogInException   on login-key pair mismatch
     */
    public static LoginedUser authUser(@Nonnull String login, @Nonnull String passwordHash) throws LogInException {
        LoginedUser loginedUser = new LoginedUser();
        
        User user = DatabaseDriver.findUser(passwordHash);
        if (user == null) {
            loginedUser.setUser(DatabaseDriver.createUser(login, passwordHash));
        } else {
            if (!login.equals(user.getLogin())) {
                logger.debug("Failed to login user {} as {}", login, user.getLogin());
                throw new LogInException(login);
            }
            loginedUser.setUser(user);
        }
        loginedUser.setAuthToken(UUID.randomUUID().toString());
        usersMap.put(loginedUser.getAuthToken(), loginedUser);
        return loginedUser;
    }
    
    @Nullable
    public static LoginedUser getLoginedUser(String authToken) {
        return usersMap.get(authToken);
    }
    
    public static void removeLoginedUser(String authToken) {
        usersMap.remove(authToken);
    }
    
    /**
     * Checks if user is authorized and return logined user on success
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
            throw new UnauthorizedAccessException(authToken);
        }
    }
    
    public static void removeSubscriber(String authToken) {
        if (usersMap.containsKey(authToken)) {
            subscribers.remove(usersMap.get(authToken));
        } else {
            throw new UnauthorizedAccessException(authToken);
        }
    }
    
    /**
     * @return  Unmodifiable view of subscribers set
     */
    public static Set<LoginedUser> getSubscribers() {
        return Collections.unmodifiableSet(subscribers);
    }
}
