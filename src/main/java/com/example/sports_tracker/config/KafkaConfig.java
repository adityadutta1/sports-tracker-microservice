package com.example.sports_tracker.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaConfig {

    @Value("${app.kafka.topic.sports-events:sports-events}")
    private String sportsEventsTopic;

    @Bean
    public NewTopic sportsEventsTopic() {
        return TopicBuilder.name(sportsEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}