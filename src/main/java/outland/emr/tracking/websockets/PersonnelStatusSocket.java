package outland.emr.tracking.websockets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import outland.emr.tracking.managers.PersonnelManager;
import outland.emr.tracking.models.PersonnelTransport;
import outland.emr.tracking.models.socket.Message;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class PersonnelStatusSocket extends TextWebSocketHandler {
    @Autowired
    private final PersonnelManager personnelManager;

    private final Set<WebSocketSession> sessionList = Collections.synchronizedSet(new HashSet<>());

    public PersonnelStatusSocket(PersonnelManager personnelManager) {
        this.personnelManager = personnelManager;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
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

    public void broadcastTracking() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        PersonnelTransport personnelTransport = personnelManager.getPersonnel();
        String personnelTransportRepresentation = objectMapper.writeValueAsString(personnelTransport);

        Message message = new Message("personnelStatus", personnelTransportRepresentation);
        broadcast(message);
    }
}
