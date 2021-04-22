package com.emr.tracing.config;

import com.emr.tracing.websockets.TracingSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocket implements WebSocketConfigurer {
    @Autowired
    private final TracingSocket tracingSocket;

    public WebSocket(TracingSocket tracingSocket) {
        this.tracingSocket = tracingSocket;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(tracingSocket, "/tracing").setAllowedOrigins("*");
    }
}
