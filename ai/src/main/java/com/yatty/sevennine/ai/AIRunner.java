package com.yatty.sevennine.ai;

import com.yatty.sevennine.client.SevenAteNineClientFactory;
import com.yatty.sevennine.client.SynchronousClient;

import java.net.InetSocketAddress;

/**
 * Starts one SevenAteNine AI.
 * Options format:
 *      AIRunner [sever-ip] [server-port] [difficulty] [games] [hosting] [players]
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
    private InetSocketAddress serverAddress;
    private boolean hosting;
    private Difficulty difficulty;
    private int games;
    private int players;
    
    
    public static void main(String[] args) {
        try {
            int players = 0;
            boolean hosting = Boolean.valueOf(args[2]);
            if (hosting) {
                players = Integer.valueOf(args[5]);
            }
            InetSocketAddress serverAddress = new InetSocketAddress(args[0], Integer.valueOf(args[1]));
            int difficulty = Integer.valueOf(args[3]);
            int games = Integer.valueOf(args[4]);
            new AIRunner(serverAddress, Difficulty.values()[difficulty], games, hosting, players).run();
        } catch (NumberFormatException e) {
            showUsage();
            System.out.println("Invalid parameters");
        } catch (IndexOutOfBoundsException e) {
            showUsage();
            System.out.println("Not enough parameters");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public AIRunner(InetSocketAddress serverAddress,
                    Difficulty difficulty, int games, boolean hosting, int players) {
        this.serverAddress = serverAddress;
        this.hosting = hosting;
        this.difficulty = difficulty;
        this.games = games;
        this.players = players;
    }
    
    public void run() {
        SevenAteNineClientFactory factory = new SevenAteNineClientFactory();
        SynchronousClient client = factory.getSynchronousClient(serverAddress);
        if (hosting) {
            new AI(client, difficulty, games).run();
        } else {
            new HostingAI(client, difficulty, games, players).run();
        }
    }
    
    private static void parseArguments(String[] args) {
        if (args.length < 5) {
            throw new IllegalArgumentException("Not enough parameters");
        }
        
    }
    
    private static void showUsage() {
        System.out.println("\tAIRunner [sever-ip] [server-port] [hosting] [difficulty] [games]");
        System.out.println("\t\tserver-ip — IP of game server");
        System.out.println("\t\tserver-port — port of game server");
        System.out.println("\t\thosting — true if bot must create new lobbies");
        System.out.println("\t\tdifficulty — integer from 0 (easy) to 3 (hard)");
        System.out.println("\t\tgames — number of games to play");
    }
}
