package com.yatty.sevennine.backend.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class FinalCleanupHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(FinalCleanupHandler.class);

//    @Override
//    public void flush(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//        ctx.channel().disconnect().sync();
////        logger.trace("Disconnecting...");
////        if (ctx.channel().isActive()) {
////            ctx.channel().disconnect().addListener((e) -> {
////                if (e.isSuccess()) {
////                    logger.trace("Disconnected");
////                } else {
////                    logger.debug("Can not disconnect: {}", e.cause());
////                }
////            }).sync();
////        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Cleanup handler caught an exception: ", cause);
    }
}
