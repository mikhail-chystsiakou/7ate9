package com.yatty.sevennine.backend.data;

import com.yatty.sevennine.backend.exceptions.data.DatabaseException;
import com.yatty.sevennine.backend.model.User;
import com.yatty.sevennine.util.PropertiesProvider;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseDriver {
    public static final Logger logger = LoggerFactory.getLogger(DatabaseDriver.class);
    private static JdbcConnectionPool connectionPool;
    private static final String DB_INITIALIZE_QUERY =
            "create table if not exists users "
            + "(id int not null auto_increment primary key, "
            + "password_hash varchar(64) unique, "
            + "login varchar(64) unique, "
            + "rating int(10))";
    private static final String CREATE_USER_QUERY =
            "insert into users (login, password_hash, rating) values (?, ?, ?)";
    private static final String FIND_USER_QUERY =
            "select login, rating from users where password_hash = ?";
    private static final String UPDATE_USER_QUERY =
            "update users set rating = ? where password_hash = ?";
    
    static {
        try {
            Properties dbProperties = PropertiesProvider.getDBProperties();
            connectionPool = JdbcConnectionPool.create(
                    "jdbc:h2:" + dbProperties.getProperty(PropertiesProvider.DB.LOCATION),
                    dbProperties.getProperty("db.login"),
                    dbProperties.getProperty(PropertiesProvider.DB.PASSWORD));
            connectionPool.getConnection().createStatement().execute(DB_INITIALIZE_QUERY);
        } catch (SQLException | IOException e) {
            logger.error("Failed to initialize DatabaseDriver", e);
        }
    }
    
    @Nullable
    public static User findUser(String passwordHash) {
        try (Connection conn = connectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(FIND_USER_QUERY);
            ps.setString(1, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setLogin(User.removeLoginSalt(rs.getString("login")));
                user.setGeneratedLogin(rs.getString("login"));
                user.setPasswordHash(passwordHash);
                user.setRating(rs.getInt("rating"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search for user by password hash", e);
        }
    }
    
    public static User createUser(String login, String passwordHash) {
        try (Connection conn = connectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(CREATE_USER_QUERY);
            String saltedLogin = login + User.generateLoginSalt();
            ps.setString(1, saltedLogin);
            ps.setString(2, passwordHash);
            ps.setInt(3, User.INITIAL_RATING);
            if (ps.executeUpdate() == 1) {
                User user = new User();
                user.setGeneratedLogin(saltedLogin);
                user.setLogin(login);
                user.setRating(User.INITIAL_RATING);
                return user;
            } else {
                throw new DatabaseException("Failed to create user '" + login + "'");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create user '" + login + "'", e);
        }
    }
    
    public static void updateUserRating(User user) {
        try (Connection conn = connectionPool.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_USER_QUERY);
            ps.setInt(1, user.getRating());
            ps.setString(2, user.getPasswordHash());
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create user '" + user.getLogin() + "'", e);
        }
    }
    
    public static void close() {
        connectionPool.dispose();
    }
}
