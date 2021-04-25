package com.emr.tracing.websockets;

import com.emr.tracing.managers.StreamManager;
import com.emr.tracing.models.Stream;
import com.emr.tracing.models.socket.Message;
import com.emr.tracing.models.socket.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TracingSocket extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(TracingSocket.class);
    private static final Set<WebSocketSession> sessionList = Collections.synchronizedSet(new HashSet<>());
    private static final AtomicLong uuids = new AtomicLong(0);
    @Autowired
    private final StreamManager streamManager;

    public TracingSocket(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        JsonNode json = new ObjectMapper().readTree(message.getPayload());

        switch (json.get("type").asText()) {
            case "join":
                User user = new User(uuids.incrementAndGet(), json.get("data").asText());
                sessionList.add(session);
                broadcastToOthers(session, new Message("join", new ObjectMapper().writeValueAsString(user)));
                Message activeBeaconMessage = new Message("activeBeacons", new ObjectMapper().writeValueAsString(streamManager.getActiveStreams()));
                emit(session, activeBeaconMessage);
                break;
            case "say":
                broadcast(new Message("say", json.get("data").asText()));
                break;
            default:
                break;
        }

    }

    private void emit(WebSocketSession session, Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private void broadcast(Message message) {
        synchronized (sessionList) {
            sessionList.forEach(session -> {
                try {
                    emit(session, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void broadcastToOthers(WebSocketSession me, Message message) {
        synchronized (sessionList) {
            sessionList.stream().filter(session -> session.equals(me))
                    .forEach(session -> {
                        try {
                            emit(session, message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void broadcastTracking(Stream data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = new Message("tracing", objectMapper.writeValueAsString(data));
        logger.info("Message sent: " + message);
        broadcast(message);
    }

    public void emitBeaconDetachment(Stream stream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = new Message("detach", objectMapper.writeValueAsString(stream));
        broadcast(message);
    }

    public void emitBeaconUpdate(Stream stream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = new Message("update", objectMapper.writeValueAsString(stream));
        broadcast(message);
    }
}
