package com.colak.springtutorial.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@EnableKafka
@Service
@RequiredArgsConstructor
public class ReactiveKafkaListener {

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;


    @Bean
    public Flux<String> kafkaMessageListener() {
        return reactiveKafkaConsumerTemplate.receiveAutoAck()
                .doOnNext(consumerRecord -> System.out.println("Received message: " + consumerRecord.value()))
                .map(ConsumerRecord::value)
                .onErrorContinue((e, o) -> System.err.println("Error processing record: " + e.getMessage()));
    }
}

