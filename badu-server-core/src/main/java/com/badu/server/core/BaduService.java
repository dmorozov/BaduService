package com.badu.server.core;

import com.badu.server.core.factories.ObjectMapperFactory;
import com.badu.server.core.netty.RequestHandlerSwitcher;
import com.badu.server.core.netty.ServerInitializer;
import com.badu.server.core.netty.handlers.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class BaduService implements ObjectMapperFactory {

    public static final int DEFAULT_SERVER_PORT = 8000;
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 512 * 1024 * 1024; // 512Mb

    private final String hostname;
    private final int port;
    private final int maxContentLength;
    private final RequestHandler[] requestHandlers;

    protected BaduService(final String hostname, final int port, final int maxContentLength, final RequestHandler[] requestHandlers) {
        this.hostname = hostname;
        this.port = port;
        this.maxContentLength = maxContentLength;
        this.requestHandlers = requestHandlers;
    }

    public static class BaduServiceBuilder {

        private String hostname;
        private int port = DEFAULT_SERVER_PORT;
        private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
        private RequestHandler[] requestHandlers;

        public BaduServiceBuilder bind(final String hostname) {
            this.hostname = hostname;
            return this;
        }
        public BaduServiceBuilder bindPort(final int port) {
            this.port = port;
            return this;
        }
        public BaduServiceBuilder maxContentLength(int maxContentLength) {
            this.maxContentLength = maxContentLength;
            return this;
        }
        public BaduServiceBuilder requestHandlers(RequestHandler[] requestHandlers) {
            this.requestHandlers = requestHandlers;
            return this;
        }

        public BaduService build() {
            return new BaduService(hostname, port, maxContentLength, requestHandlers);
        }
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer(maxContentLength,
                            new RequestHandlerSwitcher(createObjectMapper(), requestHandlers)));

            Channel ch = (null != hostname ? b.bind(hostname, port) : b.bind(port)).sync().channel();
            System.err.println("Open your web browser and navigate to http" + (null != hostname ? "://" + hostname + ":" : "://127.0.0.1:") + port + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
