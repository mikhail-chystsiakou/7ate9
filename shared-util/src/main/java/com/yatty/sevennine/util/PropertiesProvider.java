package com.yatty.sevennine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Serves as property loader and holder.
 *
 * @author Mike
 * @version 17/02/18
 */
public class PropertiesProvider {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesProvider.class);
    private static HashMap<String, Properties> propertiesCache = new HashMap<>();

    public interface Environment {
        String FILE_NAME = "environment.properties";
        String PORT = "port";
        String HOST = "host";
    }
    
    public interface Game {
        String FILE_NAME = "game.properties";
        String STALEMATE_DELAY_MILLISECONDS = "stalemate_delay_milliseconds";
    }
    
    public interface AI {
        String FILE_NAME = "ai.properties";
        String SERVER_IP = "server.ip";
        String SERVER_PORT = "server.port";
    }
    
    public interface DB {
        String FILE_NAME = "db.properties";
        String LOCATION = "db.location";
        String NAME = "db.name";
        String LOGIN = "db.login";
        String PASSWORD = "db.password";
    }

    public static Properties getEnvironmentProperties() throws IOException {
        return lookupAndGet(Environment.FILE_NAME);
    }
    
    public static Properties getGameProperties() throws IOException {
        return lookupAndGet(Game.FILE_NAME);
    }
    
    public static Properties getDBProperties() throws IOException {
        return lookupAndGet(DB.FILE_NAME);
    }
    
    /**
     * Tries to load properties from cache, loads from file
     * otherwise.
     *
     * @param fileName      properties file name
     * @return  requested properties file
     * @throws IOException if IO error occurs
     */
    public static Properties lookupAndGet(@Nonnull String fileName) throws IOException {
        if (propertiesCache.containsKey(fileName)) {
            return propertiesCache.get(fileName);
        } else {
            Properties loadedProperties = loadPropertiesFile(fileName);
            propertiesCache.put(fileName, loadedProperties);
            return loadedProperties;
        }
    }

    private static Properties loadPropertiesFile(String fileName) throws IOException {
        try (InputStream inputStream = PropertiesProvider.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (NullPointerException e) {
            throw new IOException("Can not load properties file '" + fileName + "'", e);
        }
    }
}
