package com.example.sports_tracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class SportsEventConsumer {

    @KafkaListener(topics = "${app.kafka.topic.sports-events:sports-events}")
    public void handleSportsEvent(
            @Payload Map<String, Object> message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("🎯 CONSUMED from Kafka - Topic: {}, Partition: {}, Offset: {}, Message: {}",
                topic, partition, offset, message);
    }
}