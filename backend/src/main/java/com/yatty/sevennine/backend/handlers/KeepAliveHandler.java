package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.KeepAliveRequest;
import com.yatty.sevennine.backend.exceptions.security.UnauthorizedAccessException;
import com.yatty.sevennine.backend.model.LoginedUser;
import com.yatty.sevennine.backend.model.UserRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to update user socket connection (netty channel). Peers must keep
 * connection alive in following situations:
 * <ul>
 *     <li>User was subscribed for lobbies list updates</li>
 *     <li>User is playing game and server must send notifications about
 *     game events</li>
 * </ul>
 *
 * @author mike
 * @version 13/03/18
 */
@ChannelHandler.Sharable
public class KeepAliveHandler extends SimpleChannelInboundHandler<KeepAliveRequest> {
    private static final Logger logger = LoggerFactory.getLogger(KeepAliveHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, KeepAliveRequest msg) throws Exception {
        LoginedUser user = UserRegistry.getUserByToken(msg.getAuthToken());
    
        if (user != null) {
            logger.debug("Got keepalive request from user '{}'", user.getName());
            user.setChannel(ctx.channel());
        } else {
            throw new UnauthorizedAccessException(msg.getAuthToken());
        }
    }
}
