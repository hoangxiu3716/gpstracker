package de.gimik.apps.gpstracker.backend.service;

public interface MqttService {
    void connect();
    void publish(String topic, String payload, int qos);
    boolean isConnected();
}
