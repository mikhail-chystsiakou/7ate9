package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.backend.exceptions.io.MessageSendingException;
import com.yatty.sevennine.backend.model.Player;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;

public class PlayerMessageSender {
    private static final Logger logger = LoggerFactory.getLogger(PlayerMessageSender.class);

    /**
     * Sends <code>message</code> to the <code>playerAddress</code> by the
     * <code>channel</code>.
     *
     * @param channel           channel to send messages.
     * @param playerAddress     address to send message to
     * @param message           message to send
     * @throws MessageSendingException  if any technical error occurs
     */
    public static void sendMessage(Channel channel, InetSocketAddress playerAddress, Object message) {
        try {
            if (channel.isActive() && !playerAddress.equals(channel.remoteAddress())) {
                channel.disconnect().sync();
            }
            channel.connect(playerAddress).sync();
            channel.writeAndFlush(message).sync();
        } catch (InterruptedException e) {
            throw new MessageSendingException("Interrupted during sending message to "
                    + playerAddress, e);
        }
    }

    /**
     * Sends <code>message</code> to the <code>player</code> by the
     * <code>channel</code>.
     *
     * @param channel   channel to send messages.
     * @param player    players to send message to
     * @param message   message to send
     * @throws MessageSendingException  if any technical error occurs
     */
    public static void sendMessage(Channel channel, Player player, Object message) {
        sendMessage(channel, player.getRemoteAddress(), message);
    }

    /**
     * Sends <code>message</code> to the <code>players</code> by the
     * <code>channel</code> <b>one by one</b>.
     * Note, that channel will be unbound from current pear.
     *
     * @param channel   channel to send messages.
     * @param players   list of players to send message to
     * @param message   message to send
     * @throws MessageSendingException  if any technical error occurs
     */
    public static void broadcast(Channel channel, Collection<Player> players, Object message) {
        for(Player p : players) {
            sendMessage(channel, p.getRemoteAddress(), message);
        }
    }
}
