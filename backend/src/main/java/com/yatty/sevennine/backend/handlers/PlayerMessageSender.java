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
            logger.debug("Sending message. active: {}, open: {}, remote address {} , playerAddress: {}",
                    channel.isActive(), channel.isOpen(), channel.remoteAddress(), playerAddress);
            channel.disconnect().sync();
            channel.connect(playerAddress).sync();
//            if (!playerAddress.equals(channel.remoteAddress())) {
//                channel.disconnect().sync();
//                logger.debug("Channel disconnected. active: {}, open: {}", channel.isActive(), channel.isOpen());
//                channel.connect(playerAddress).sync();
//                logger.debug("Channel reconnected");
//            } else if (!channel.isActive()) {
//                channel.connect(playerAddress).sync();
//                logger.debug("Channel reconnected");
//            }
            channel.writeAndFlush(message).sync();
        } catch (InterruptedException e) {
            throw new MessageSendingException("Interrupted during sending message to "
                    + playerAddress, e);
        } catch (Exception e) {
            logger.warn("Exception during sending message: ", e);
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
