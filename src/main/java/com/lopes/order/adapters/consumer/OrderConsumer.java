package com.lopes.order.adapters.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void consume(String message) {
        // Lógica para consumir pedidos.
    }
}

