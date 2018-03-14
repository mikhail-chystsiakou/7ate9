package com.yatty.sevennine.backend.handlers.auth;

import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.backend.model.UserRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LogInHandler extends SimpleChannelInboundHandler<LogInRequest> {
    private static final Logger logger = LoggerFactory.getLogger(LogInHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogInRequest msg) throws Exception {
        logger.debug("User '{}' is logging in", msg.getName());
        
        String authToken = UserRegistry.registerUser(msg.getName());
        
        LogInResponse response = new LogInResponse();
        response.setAuthToken(authToken);
    
        ctx.channel().writeAndFlush(response);
    }
}