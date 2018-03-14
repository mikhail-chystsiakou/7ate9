package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.TestRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class TestHandler extends SimpleChannelInboundHandler<TestRequest> {
    private static final Logger logger = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TestRequest msg) throws Exception {

        ctx.channel().writeAndFlush(msg.getResponseData()).addListener(e -> {
            if (e.isSuccess()) {
                logger.debug("Successfully sent test message '{}' back to the client", msg.getResponseData());
            } else {
                logger.warn("Exception during sending test response: ", e.cause());
            }
        });
    }
}