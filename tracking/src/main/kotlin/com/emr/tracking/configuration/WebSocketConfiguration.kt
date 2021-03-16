package com.emr.tracking.configuration

import com.emr.tracking.websocket.TrackingSocket
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfiguration : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        println("WebSocket registered")
        registry
            .addHandler(tracingHandler(), "/tracing")
            .setAllowedOrigins("*")
    }

    @Bean
    fun tracingHandler(): TrackingSocket {
        return TrackingSocket()
    }
}