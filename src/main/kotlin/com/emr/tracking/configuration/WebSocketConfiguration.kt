package com.emr.tracking.configuration

import com.emr.tracking.websocket.TrackingSocket
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfiguration(
    private val trackingSocket: TrackingSocket
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        println("WebSocket registered")
        registry
            .addHandler(trackingSocket, "/tracing")
            .setAllowedOrigins("*")
    }
}