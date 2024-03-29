package com.badu.server.core.netty.codecs.websockets;

import com.badu.server.core.ApiResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class WebSocketsApiResponseEncoder extends MessageToMessageEncoder<ApiResponse> {

    public WebSocketsApiResponseEncoder() {
        super(ApiResponse.class);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ApiResponse msg, List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(msg.content()));
    }

}
