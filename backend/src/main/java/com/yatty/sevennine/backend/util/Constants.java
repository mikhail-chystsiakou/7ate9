package com.yatty.sevennine.backend.util;

import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public interface Constants {
    AttributeKey<InetSocketAddress> PEER_ADDRESS_KEY = AttributeKey.newInstance("peerAddress");
}
