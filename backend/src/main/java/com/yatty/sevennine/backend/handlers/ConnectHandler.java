package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.ConnectResponse;
import com.yatty.sevennine.backend.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;

public class ConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest msg) throws Exception {
        System.out.println("Connecting..");
        InetSocketAddress peerAddress = ctx.channel().attr(Constants.PEER_ADDRESS_KEY).get();
        System.out.println("Got message from: " + peerAddress);
        ctx.channel().connect(peerAddress).addListener((e) -> {
            if (e.isSuccess()) {
                System.out.println("Connected to peer");
            } else {
                System.out.println("Failed to connect");
                e.cause().printStackTrace();
            }
        }).sync();
        ctx.channel().writeAndFlush(new ConnectResponse(true)).addListener((e) -> {
            if (e.isSuccess()) {
                System.out.println("Response sent");
            } else {
                e.cause().printStackTrace();
            }
        }).sync();
    }
}