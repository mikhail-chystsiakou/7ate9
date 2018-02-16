package com.yatty.sevennine.backend.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalCleanupHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(FinalCleanupHandler.class);

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        logger.trace("Disconnecting...");
        ctx.channel().disconnect().addListener((e) -> {
            if (e.isSuccess()) {
                logger.debug("Disconnected");
            } else {
                logger.debug("Can not disconnect: {}", e.cause());
            }
        }).sync();
    }
}
