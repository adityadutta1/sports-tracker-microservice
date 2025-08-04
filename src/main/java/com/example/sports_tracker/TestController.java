package com.example.sports_tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @GetMapping("/")
    public String home() {
        return "Sports Tracker Application is running! 🏃‍♂️";
    }

    @GetMapping("/test-kafka")
    public String testKafka() {
        Map<String, Object> message = new HashMap<>();
        message.put("event", "test");
        message.put("timestamp", System.currentTimeMillis());
        message.put("data", "Hello from Sports Tracker!");

        kafkaPublisher.publishMessage("test-topic", "test-key", message);

        return "Kafka message sent! Check your logs.";
    }
}