package com.example.wateringapp.MqttManager;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MqttManager {

    public interface MessageListener {
        void onMessageReceived(String topic, String payload);
    }

    private static MqttManager instance;

    private final Mqtt3AsyncClient client;
    private final List<MessageListener> listeners = new ArrayList<>();
    private MqttManager() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost("x")
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildAsync();
    }

    public static synchronized MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }

    public void connect() {
        client.connectWith()
                .simpleAuth()
                .username("Plantmonitor123")
                .password("Plantmonitor123".getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        System.out.println("MQTT Connection failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Connected to HiveMQ Cloud");
                    }
                });
    }

    public void subscribe(String topic) {
        try {
            client.subscribeWith()
                    .topicFilter(topic)
                    .callback(this::handleMessage)
                    .send();  // no future, can't log actual result
            System.out.println("Sent subscribe request for topic: " + topic);
        } catch (Exception e) {
            System.out.println("Exception while subscribing to topic: " + topic + " — " + e.getMessage());
        }
    }


    public void publish(String topic, String message) {
        System.out.println("Publishing to " + topic + ": " + message);
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send();
    }

    public void setMessageListener(MessageListener messageListener) {
        if (!listeners.contains(messageListener)) {
            listeners.add(messageListener);
            System.out.println("MQTT listener added!");
        } else {
            System.out.println("MQTT listener already exists!");
        }
    }

    private void handleMessage(Mqtt3Publish publish) {
        String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
        System.out.println("Received on topic " + publish.getTopic() + ": " + payload);

        for (MessageListener listener : listeners) {
            listener.onMessageReceived(publish.getTopic().toString(), payload);
        }
    }

    public void connectWithCallback(Runnable onConnected) {
        if (client.getState().isConnected()) {
            onConnected.run();
        } else {
            client.connectWith()
                    .simpleAuth()
                    .username("Plantmonitor123")
                    .password("Plantmonitor123".getBytes(StandardCharsets.UTF_8))
                    .applySimpleAuth()
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            System.out.println("MQTT Connection failed: " + throwable.getMessage());
                        } else {
                            System.out.println("Connected to HiveMQ Cloud");

                            restoreSubscriptions();

                            onConnected.run();
                        }
                    });
        }
    }
    public void restoreSubscriptions() {
        if (!listeners.isEmpty()) {
            System.out.println("Resubscribing and reattaching callback");

            subscribe("plants/soil");
            subscribe("plants/temp");
            subscribe("plants/humidity");
            subscribe("plants/watering");
            subscribe("plants/waterLevel");
        } else {
            System.out.println("No listeners found during restoreSubscriptions");
        }
    }



}

