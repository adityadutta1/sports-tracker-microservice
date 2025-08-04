package com.example.sports_tracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    public KafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMessage(String topic, String key, Map<String, Object> message) {
        if (kafkaEnabled) {
            try {
                log.info("📡 Publishing to REAL Kafka - Topic: {}, Key: {}", topic, key);

                CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ SUCCESS: Published to Kafka - Topic: {}, Key: {}, Partition: {}, Offset: {}",
                                topic, key,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ FAILED: Publishing to Kafka - Topic: {}, Key: {}", topic, key, ex);
                        logMockMessage(topic, key, message); // Fallback to mock
                    }
                });
            } catch (Exception e) {
                log.error("❌ Exception publishing to Kafka", e);
                logMockMessage(topic, key, message); // Fallback to mock
            }
        } else {
            logMockMessage(topic, key, message);
        }
    }

    private void logMockMessage(String topic, String key, Map<String, Object> message) {
        log.info("📤 MOCK KAFKA MESSAGE - Topic: {}, Key: {}, Message: {}", topic, key, message);
    }
}