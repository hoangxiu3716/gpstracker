package de.gimik.apps.gpstracker.backend.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gimik.apps.gpstracker.backend.service.BackendServiceRegistry;
import de.gimik.apps.gpstracker.backend.service.MqttService;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/mqtt-bridge")
public class MqttWebSocketBridge {

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Error handling
        onClose(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        MqttService mqttService = BackendServiceRegistry.getMqttService();
        if (mqttService == null) {
            // Service not available yet
            return;
        }

        try {
            // Expect messages from client to be commands to publish to MQTT
            // Format: { "topic": "...", "payload": {...}, "qos": 0 }
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String topic = (String) messageMap.get("topic");
            Object payload = messageMap.get("payload");

            // Read QoS from message, default to 1 for backward compatibility
            int qos = messageMap.containsKey("qos") ? ((Number) messageMap.get("qos")).intValue() : 1;

            if (topic != null && payload != null) {
                String payloadJson = objectMapper.writeValueAsString(payload);
                mqttService.publish(topic, payloadJson, qos);
            }
        } catch (Exception e) {
            // Malformed JSON or other error
        }
    }

    public static void broadcast(String message) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    // Handle error, e.g., by closing the session
                }
            }
        }
    }
}
