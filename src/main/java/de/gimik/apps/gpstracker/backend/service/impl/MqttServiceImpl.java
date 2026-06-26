package de.gimik.apps.gpstracker.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gimik.apps.gpstracker.backend.service.BackendServiceRegistry;
import de.gimik.apps.gpstracker.backend.service.MqttService;
import de.gimik.apps.gpstracker.backend.web.websocket.MqttWebSocketBridge;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

@Service
public class MqttServiceImpl implements MqttService, MqttCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MqttServiceImpl.class);

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.broker.username}")
    private String username;

    @Value("${mqtt.broker.password}")
    private String password;

    private String clientId;

    private IMqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Flag to manage reconnection thread
    private volatile boolean isReconnecting = false;

    @PostConstruct
    public void init() {
        // Generate a unique client ID for this instance to avoid conflicts
        this.clientId = "gpstracker-backend-" + UUID.randomUUID().toString();
        LOG.info("Generated MQTT Client ID: {}", this.clientId);

        // Register this instance with the static registry so the WebSocket endpoint can access it.
        BackendServiceRegistry.setMqttService(this);

        // Initial connection attempt
        connect();
    }

    @Override
    public synchronized void connect() {
        // If already connected, do nothing
        if (mqttClient != null && mqttClient.isConnected()) {
            return;
        }

        try {
            // Close existing client to avoid memory leaks before creating a new one
            if (mqttClient != null) {
                try {
                    mqttClient.close();
                } catch (Exception e) {
                    LOG.warn("Error closing existing client before reconnect: {}", e.getMessage());
                }
            }

            // Create new client instance
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(brokerUrl, clientId, persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Disable Paho's automatic reconnect to use our custom logic (more reliable for CleanSession=true)
            connOpts.setAutomaticReconnect(false);
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(30);
            connOpts.setKeepAliveInterval(60);

            // Configure TLS
            if (brokerUrl.startsWith("ssl://")) {
                SSLSocketFactory sslSocketFactory = createSslSocketFactory();
                connOpts.setSocketFactory(sslSocketFactory);
            }

            mqttClient.setCallback(this);

            LOG.info("Connecting to MQTT broker: {}", brokerUrl);
            mqttClient.connect(connOpts);
            LOG.info("Successfully connected to MQTT broker.");

            // Subscribe to topics after successful connection
            subscribeToTopics();

            // Reset reconnection flag upon success
            isReconnecting = false;

        } catch (Exception e) {
            LOG.error("Failed to connect to MQTT broker: {}", e.getMessage());
            // Trigger reconnection loop if this was an initial attempt or manual call
            handleReconnection();
        }
    }

    private void subscribeToTopics() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                LOG.info("Subscribing to topics...");




//                mqttClient.subscribe("gpstracker/robot/+/resp/");
//
//                mqttClient.subscribe("gpstracker/robot/+/telemetry");
//                mqttClient.subscribe("gpstracker/robot/+/status/system");
//                mqttClient.subscribe("gpstracker/robot/+/status/connection");
//                mqttClient.subscribe("gpstracker/robot/+/status/navigation");
//                mqttClient.subscribe("gpstracker/robot/+/resp/+");
//                mqttClient.subscribe("gpstracker/robot/+/status/safety");
                mqttClient.subscribe("gpstracker/robot/+/status/connection");
                mqttClient.subscribe("gpstracker/robot/+/resp/robot");
                mqttClient.subscribe("gpstracker/robot/+/resp/symovo");
                mqttClient.subscribe("gpstracker/robot/+/status/navigation");
                mqttClient.subscribe("gpstracker/robot/+/status/system");
                mqttClient.subscribe("gpstracker/robot/+/telemetry");
                mqttClient.subscribe("gpstracker/robot/+/status/safety");
            }
        } catch (MqttException e) {
            LOG.error("Error subscribing to topics", e);
        }
    }

    private SSLSocketFactory createSslSocketFactory() throws Exception {
        InputStream caInput = new BufferedInputStream(MqttServiceImpl.class.getResourceAsStream("/mqtt-broker-ca.crt"));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate ca = (X509Certificate) cf.generateCertificate(caInput);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("ca", ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(null, tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    @PreDestroy
    public void disconnect() {
        isReconnecting = false; // Stop reconnection loop if active
        if (mqttClient == null) {
            return;
        }
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
                LOG.info("Disconnected from MQTT broker.");
            }
            mqttClient.close();
            LOG.info("MQTT client closed and resources released.");
        } catch (MqttException e) {
            LOG.error("Error while disconnecting or closing MQTT client", e);
        }
    }

    @Override
    public void publish(String topic, String payload, int qos) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            LOG.error("MQTT client is not connected. Cannot publish message.");
            return;
        }
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
            LOG.debug("Published message to topic '{}': {}", topic, payload);
        } catch (MqttException e) {
            LOG.error("Failed to publish message to topic: " + topic, e);
        }
    }

    @Override
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.warn("MQTT connection lost! Cause: {}", cause != null ? cause.getMessage() : "Unknown");

        // Notify WebSocket clients about connection loss
        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("topic", "system/connection");
        messageNode.put("payload", "lost");
        MqttWebSocketBridge.broadcast(messageNode.toString());

        // Start reconnection logic
        handleReconnection();
    }

    private void handleReconnection() {
        if (isReconnecting) {
            return; // Prevent multiple reconnection threads
        }
        isReconnecting = true;

        new Thread(() -> {
            LOG.info("Starting reconnection loop...");
            while (isReconnecting) {
                try {
                    LOG.info("Attempting to reconnect to MQTT broker in 5 seconds...");
                    Thread.sleep(5000); // Wait 5 seconds before retrying

                    // Try to connect again
                    connect();

                    // If connected successfully, break the loop
                    if (mqttClient != null && mqttClient.isConnected()) {
                        LOG.info("Reconnection successful!");
                        isReconnecting = false;

                        // Notify WebSocket clients that connection is back
                        ObjectNode messageNode = objectMapper.createObjectNode();
                        messageNode.put("topic", "system/connection");
                        messageNode.put("payload", "connected");
                        MqttWebSocketBridge.broadcast(messageNode.toString());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isReconnecting = false;
                } catch (Exception e) {
                    LOG.error("Reconnection attempt failed: {}", e.getMessage());
                    // Loop will continue and try again
                }
            }
        }).start();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        LOG.info("Message arrived on topic '{}', forwarding to WebSocket clients.", topic);

        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("topic", topic);
        try {
            messageNode.set("payload", objectMapper.readTree(payload));
        } catch (IOException e) {
            messageNode.put("payload", payload);
        }

        MqttWebSocketBridge.broadcast(messageNode.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.debug("Delivery complete for message: {}", token.getMessageId());
    }
}