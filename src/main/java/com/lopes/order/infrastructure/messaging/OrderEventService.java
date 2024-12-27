package com.lopes.order.infrastructure.messaging;

import com.lopes.order.domain.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.order-events}")
    private String orderEventsTopic;

    public void publishOrderEvent(OrderEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
            orderEventsTopic,
            event.getEventId(),
            event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order event sent successfully. Event ID: {}, Topic: {}, Partition: {}, Offset: {}",
                    event.getEventId(),
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error("Failed to send order event. Event ID: {}", event.getEventId(), ex);
            }
        });
    }
}
