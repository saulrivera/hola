package outland.emr.tracking.config;

import outland.emr.tracking.websockets.NotificationSocket;
import outland.emr.tracking.websockets.TracingSocket;
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
    @Autowired
    private final NotificationSocket notificationSocket;

    public WebSocket(TracingSocket tracingSocket, NotificationSocket notificationSocket) {
        this.tracingSocket = tracingSocket;
        this.notificationSocket = notificationSocket;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(tracingSocket, "/tracing").setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(notificationSocket, "/notification").setAllowedOrigins("*");
    }
}

