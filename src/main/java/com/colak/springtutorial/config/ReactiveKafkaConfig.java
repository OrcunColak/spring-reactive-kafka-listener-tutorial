package com.colak.springtutorial.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReactiveKafkaConfig {

    private final KafkaProperties kafkaProperties;

    private final KafkaAdmin kafkaAdmin;

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions() {
        var topicExists = doesTopicExists(topic);

        // Use KafkaProperties to automatically load configuration from application.yaml
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry());
        if (!topicExists)
            throw new IllegalArgumentException("Topic does not exist: " + topic);

        return ReceiverOptions.<String, String>create(consumerProperties)
                .subscription(Collections.singleton(topic));
    }

    // Verifies if a topic exists
    private boolean doesTopicExists(String topicName) {

        boolean result = false;
        Map<String, Object> configurationProperties = kafkaAdmin.getConfigurationProperties();
        try (AdminClient adminClient = AdminClient.create(configurationProperties)) {
            result = adminClient.listTopics()
                    .names()
                    .get()
                    .stream()
                    .anyMatch(topic -> topic.equalsIgnoreCase(topicName));
        } catch (Exception _) {
        }
        return result;
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate(ReceiverOptions<String, String> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}

