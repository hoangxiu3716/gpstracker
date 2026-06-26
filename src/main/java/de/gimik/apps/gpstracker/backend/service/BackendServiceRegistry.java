package de.gimik.apps.gpstracker.backend.service;

import org.springframework.stereotype.Component;

/**
 * A static holder for Spring-managed services that need to be accessed
 * by non-Spring-managed components, like JSR-356 WebSocket endpoints.
 */
@Component
public class BackendServiceRegistry {

    private static MqttService mqttService;

    public static MqttService getMqttService() {
        return mqttService;
    }

    public static void setMqttService(MqttService service) {
        mqttService = service;
    }
}
