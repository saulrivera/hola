package com.emr.tracing.websockets;

import com.emr.tracing.models.socket.Stream;
import com.emr.tracing.models.socket.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class NotificationSocket extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessionList = Collections.synchronizedSet(new HashSet<>());
    private final AtomicLong uuids = new AtomicLong(0);

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        JsonNode node = new ObjectMapper().readTree(message.getPayload());
        if ("join".equals(node.get("type").asText())) {
            synchronized (sessionList) {
                sessionList.add(session);
            }
        }
    }

    private void emit(WebSocketSession session, Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    public void emitBeaconAlert(Stream stream) {
        synchronized (sessionList) {
            sessionList.forEach(session -> {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    emit(session, new Message("beaconAlert", objectMapper.writeValueAsString(stream)));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }
}
