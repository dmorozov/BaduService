package com.badu.server.core.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final int maxContentLength;

    public ServerInitializer(final int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpContentCompressor());
        p.addLast(new HttpObjectAggregator(maxContentLength));
        p.addLast(new ServerHandler());
    }
}
