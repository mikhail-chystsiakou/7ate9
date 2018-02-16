package com.yatty.sevennine.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesProvider {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesProvider.class);
    private static HashMap<String, Properties> propertiesCache = new HashMap<>();

    public interface Environment {
        String FILE_NAME = "environment.properties";
        String PORT = "port";
        String HOST = "host";
    }

    public static Properties getEnvironmentProperties() throws IOException {
        if (propertiesCache.containsKey(Environment.FILE_NAME)) {
            return propertiesCache.get(Environment.FILE_NAME);
        } else {
            Properties loadedProperties = loadPropertiesFile(Environment.FILE_NAME);
            propertiesCache.put(Environment.FILE_NAME, loadedProperties);
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
