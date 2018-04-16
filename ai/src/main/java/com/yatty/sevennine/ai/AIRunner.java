package com.yatty.sevennine.ai;

import com.yatty.sevennine.client.SevenAteNineClientFactory;
import com.yatty.sevennine.client.SynchronousClient;
import com.yatty.sevennine.util.PropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Starts one SevenAteNine AI.
 * Options format:
// *      AIRunner [sever-ip] [server-port] [difficulty] [games] [hosting] [players]
 *      AIRunner [difficulty] [games] [hosting] [players]
 * <ul>
 *     <li>server-ip — IP of game server</li>
 *     <li>server-port — port of game server</li>
 *     <li>difficulty — integer from 0 (easy) to 3 (hard)</li>
 *     <li>games — number of games to play</li>
 *     <li>hosting — true if bot must create new lobbies</li>
 *     <li>players — number of players in hosted games</li>
 * </ul>
 * @author mike
 * @version 10.03.18
 */
public class AIRunner {
    public static final Logger logger = LoggerFactory.getLogger(AIRunner.class);
    
    private InetSocketAddress serverAddress;
    private boolean hosting = false;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private int games = 1;
    private int players = 2;
    
//    private static final int SERVER_IP_PARAM = 0;
//    private static final int SERVER_PORT_PARAM = 1;
//    private static final int DIFFICULTY_PARAM = 2;
//    private static final int GAMES_NUMBER_PARAM = 3;
//    private static final int HOSTING_PARAM = 4;
//    private static final int PLAYERS_PARAM = 5;
    
    private static final int DIFFICULTY_PARAM = 0;
    private static final int GAMES_NUMBER_PARAM = 1;
    private static final int HOSTING_PARAM = 2;
    private static final int PLAYERS_PARAM = 3;
    
    public static void main(String[] args) {
        AIRunner runner = new AIRunner();
        try {
            runner.setHosting(Boolean.valueOf(args[HOSTING_PARAM]));
            if (runner.hosting) {
                runner.setPlayers(Integer.valueOf(args[PLAYERS_PARAM]));
            }
            Properties aiProperties = PropertiesProvider.lookupAndGet(PropertiesProvider.AI.FILE_NAME);
            
            runner.setServerAddress(new InetSocketAddress(
//                    aiProperties.getProperty(PropertiesProvider.AI.SERVER_IP),
                    aiProperties.getProperty("server.ip"),
                    Integer.valueOf(aiProperties.getProperty(PropertiesProvider.AI.SERVER_PORT))
            ));
            runner.setDifficulty(Difficulty.values()[Integer.valueOf(args[DIFFICULTY_PARAM])]);
            runner.setGames(Integer.valueOf(args[GAMES_NUMBER_PARAM]));
            runner.run();
        } catch (NumberFormatException e) {
            showUsage();
            System.out.println("Invalid parameters");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            showUsage();
            System.out.println("Not enough parameters");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        SevenAteNineClientFactory factory = new SevenAteNineClientFactory();
        factory.setExceptionHandler(Throwable::printStackTrace);
        SynchronousClient client = factory.getSynchronousClient(serverAddress);
        client.start();
        if (hosting) {
            new HostingAI(client, difficulty, games, players).run();
        } else {
            new AI(client, difficulty, games).run();
        }
        client.stop();
    }
    
    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public void setHosting(boolean hosting) {
        this.hosting = hosting;
    }
    
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public void setGames(int games) {
        this.games = games;
    }
    
    public void setPlayers(int players) {
        this.players = players;
    }
    
    private static void showUsage() {
        System.out.println("\tAIRunner [sever-ip] [server-port] [difficulty] [games] [hosting] [players]");
        System.out.println("\t\tserver-ip — IP of game server");
        System.out.println("\t\tserver-port — port of game server");
        System.out.println("\t\tdifficulty — integer from 0 (easy) to 3 (hard)");
        System.out.println("\t\tgames — number of games to play");
        System.out.println("\t\thosting — true if bot must create new lobbies");
        System.out.println("\t\tplayers — number of players in hosted games");
    }
}
