package com.badu;

import com.badu.server.core.BaduService;
import com.badu.server.core.netty.handlers.*;

public class ExampleService {

    public static void main(String[] args) {
        /*
        * 1. Request handlers are executed in order
        * 2. NotFoundHandler should check for the requested content type and generate correspondent response (html, json)
        * 3. Check ApiProtocolSwitcher as example for the implementation
        * */
        final BaduService service = new BaduService.BaduServiceBuilder()
                .bind("127.0.0.1")
                .bindPort(8080)
                .requestHandlers(new RequestHandler[] {
                        new WebSocketHandler("/channel1", new CustomWebsocketHandler1()),
                        new WebSocketHandler("/channel2", new CustomWebsocketHandler2()),
                        new UploadHandler("/upload", new CustomUploadHandler()),
                        new RestApiHandler("/api", new CustomRestApiHandler()),
                        new StaticContentHandler("/", new ClasspathStaticContentHandler("public")),
                        new NotFoundHandler()
                })
                .build();
        try {
            service.start();
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
