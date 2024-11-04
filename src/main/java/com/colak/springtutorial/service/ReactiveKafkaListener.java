package com.colak.springtutorial.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ReactiveKafkaListener {

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;

    public void startListening() {
        processKafkaMessages()
                .subscribe(
                        this::handleMessage,
                        error -> System.err.println("Error processing message: " + error.getMessage())
                );
    }

    private Flux<String> processKafkaMessages() {
        return reactiveKafkaConsumerTemplate.receiveAutoAck()
                .map(ConsumerRecord::value);  // Extract the message value from ConsumerRecord
    }

    private void handleMessage(String message) {
        // Implement your message processing logic here
        System.out.println("Processing message: " + message);

        // Add any additional logic you need for processing the message
    }

    @PostConstruct
    public void init() {
        startListening();
    }
}

