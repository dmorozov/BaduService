package com.badu.server.core.netty;

import com.badu.server.core.logging.MDCLogging;
import com.badu.server.core.netty.codecs.RESTCodec;
import com.badu.server.core.netty.codecs.websockets.WebSocketsApiRequestDecoder;
import com.badu.server.core.netty.codecs.websockets.WebSocketsApiResponseEncoder;
import com.badu.server.core.netty.codecs.websockets.WebSocketsServerProtocolUpdater;
import com.badu.server.core.netty.handlers.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class RequestHandlerSwitcher extends MessageToMessageDecoder<FullHttpRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandlerSwitcher.class);

    private final ObjectMapper objectMapper;
    private final CorsConfig corsConfig;
    private static final ChannelHandler webSocketsServerProtocolUpdater = new WebSocketsServerProtocolUpdater();
    private static final ChannelHandler webSocketsApiResponseEncoder = new WebSocketsApiResponseEncoder();
    private static final ChannelHandler restCodec = new RESTCodec();

    private final RequestHandler[] handlers;

    public RequestHandlerSwitcher(ObjectMapper objectMapper, RequestHandler[] handlers) {
        super();

        this.handlers = handlers;
        this.objectMapper = objectMapper;
        this.corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowCredentials()                                     // required for custom headers
                .allowedRequestMethods(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS)
                .maxAge(1 * 60 * 60)                                    // 1 hour
                .allowedRequestHeaders(
                        HttpHeaderNames.CONTENT_TYPE.toString(),
                        RESTCodec.HEADER_REQUEST_ID,                    // header for tracking request ID
                        HttpHeaderNames.AUTHORIZATION.toString())       // header for OAuth2 authentication
                .exposeHeaders(RESTCodec.HEADER_REQUEST_ID)
                .build();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        ChannelPipeline pipeline = ctx.pipeline();

        if (msg.uri().equals("/websocket")) {
            LOGGER.debug("Switching to WebSockets pipeline...");
            pipeline.addAfter(ctx.name(), "ws-protocol-updater", webSocketsServerProtocolUpdater);
            pipeline.addAfter("ws-protocol-updater", "api-response-encoder-ws", webSocketsApiResponseEncoder);
            pipeline.addAfter("api-response-encoder-ws", "api-request-decoder-ws", new WebSocketsApiRequestDecoder(objectMapper));
            pipeline.remove(this);
        } else {
            LOGGER.debug("Switching to REST pipeline...");
            pipeline.addAfter(ctx.name(), "cors", new CorsHandler(corsConfig));
            pipeline.addAfter("cors", "rest-codec", restCodec);
            pipeline.remove(this);
        }

        out.add(ReferenceCountUtil.retain(msg));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        String clientAddr;
        if (remoteAddress instanceof InetSocketAddress) {
            clientAddr = ((InetSocketAddress) remoteAddress).getHostString();
        } else {
            clientAddr = remoteAddress.toString();
        }
        MDC.put(MDCLogging.MDC_CLIENT_ADDR, clientAddr);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        MDC.remove(MDCLogging.MDC_CLIENT_ADDR);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Can't setup API pipeline", cause);
        ctx.close();
    }

}
