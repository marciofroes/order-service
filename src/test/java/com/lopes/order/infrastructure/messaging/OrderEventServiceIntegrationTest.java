package com.lopes.order.infrastructure.messaging;

import com.lopes.order.domain.event.OrderEvent;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"order-events-topic"})
class OrderEventServiceIntegrationTest {

    private static final String TOPIC = "order-events-topic";

    @Autowired
    private OrderEventService orderEventService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, OrderEvent> consumer;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", 
            () -> "localhost:${embedded.kafka.brokerPort}");
    }

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.lopes.order.domain.event");

        DefaultKafkaConsumerFactory<String, OrderEvent> cf = new DefaultKafkaConsumerFactory<>(
            consumerProps,
            new StringDeserializer(),
            new JsonDeserializer<>(OrderEvent.class, false)
        );

        consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, TOPIC);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldPublishOrderCreatedEvent() {
        // Arrange
        var clock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.systemDefault());
        var order = Order.create(
            "customer123",
            List.of("prod1"),
            BigDecimal.TEN,
            clock
        );

        var event = OrderEvent.orderCreated(
            order.id(),
            order.customerId(),
            order.productIds(),
            order.totalValue(),
            order.status(),
            order.createdAt()
        );

        // Act
        orderEventService.publishOrderEvent(event);

        // Assert
        ConsumerRecord<String, OrderEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(event.getEventId());
        assertThat(record.value().getEventType()).isEqualTo("ORDER_CREATED");
        assertThat(record.value().getData().getOrderId()).isEqualTo(order.id());
        assertThat(record.value().getData().getCustomerId()).isEqualTo(order.customerId());
        assertThat(record.value().getData().getStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
