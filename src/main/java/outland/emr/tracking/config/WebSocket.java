package outland.emr.tracking.config;

import outland.emr.tracking.websockets.NotificationSocket;
import outland.emr.tracking.websockets.PersonnelStatusSocket;
import outland.emr.tracking.websockets.TrackingSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocket implements WebSocketConfigurer {
    @Autowired
    private final TrackingSocket trackingSocket;
    @Autowired
    private final NotificationSocket notificationSocket;
    @Autowired
    private final PersonnelStatusSocket personnelStatusSocket;

    public WebSocket(
            TrackingSocket trackingSocket,
            NotificationSocket notificationSocket,
            PersonnelStatusSocket personnelStatusSocket) {
        this.trackingSocket = trackingSocket;
        this.notificationSocket = notificationSocket;
        this.personnelStatusSocket = personnelStatusSocket;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(trackingSocket, "/tracking").setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(notificationSocket, "/notification").setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(personnelStatusSocket, "/personnelStatus").setAllowedOrigins("*");
    }
}

