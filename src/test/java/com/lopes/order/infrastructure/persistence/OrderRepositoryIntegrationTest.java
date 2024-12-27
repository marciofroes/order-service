package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.model.Order;
import com.lopes.order.infrastructure.persistence.entity.OrderEntity;
import com.lopes.order.infrastructure.persistence.mapper.OrderMapper;
import com.lopes.order.infrastructure.persistence.repository.MongoOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import({OrderRepositoryImpl.class, OrderMapper.class})
class OrderRepositoryIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

    @Configuration
    static class TestConfig {
        private static Clock clock;

        static void setClock(Clock newClock) {
            clock = newClock;
        }

        @Bean
        @Primary
        Clock testClock() {
            return clock;
        }
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private OrderRepositoryImpl orderRepository;

    @Autowired
    private MongoOrderRepository mongoRepository;

    @AfterEach
    void cleanup() {
        mongoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar pedido no MongoDB corretamente")
    void shouldSaveOrderCorrectly() {
        // Arrange
        var now = Instant.parse("2024-01-01T10:00:00Z");
        TestConfig.setClock(Clock.fixed(now, ZoneId.systemDefault()));

        var order = Order.create(
            "customer123",
            List.of("prod1", "prod2"),
            BigDecimal.valueOf(100.0),
            Clock.fixed(now, ZoneId.systemDefault())
        );
        
        // Act
        orderRepository.save(order);

        // Assert
        OrderEntity savedEntity = mongoRepository.findById(order.id()).orElseThrow();
        assertThat(savedEntity.getId()).isEqualTo(order.id());
        assertThat(savedEntity.getCustomerId()).isEqualTo(order.customerId());
        assertThat(savedEntity.getProductIds()).isEqualTo(order.productIds());
        assertThat(savedEntity.getTotalValue()).isEqualByComparingTo(order.totalValue());
        assertThat(savedEntity.getOrderStatus()).isEqualTo(order.status());
        assertThat(savedEntity.getCreatedAt()).isEqualTo(now.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Test
    @DisplayName("Deve detectar pedido duplicado dentro da janela de tempo")
    void shouldDetectDuplicateOrderWithinTimeWindow() {
        // Arrange
        var now = Instant.parse("2024-01-01T10:00:00Z");
        TestConfig.setClock(Clock.fixed(now, ZoneId.systemDefault()));

        var order = Order.create(
            "customer123",
            List.of("prod1", "prod2"),
            BigDecimal.valueOf(100.0),
            Clock.fixed(now, ZoneId.systemDefault())
        );
        
        orderRepository.save(order);

        // Act
        var hasDuplicate = orderRepository.existsByCustomerIdAndCreatedAtAfter(
            order.customerId(),
            now.atZone(ZoneId.systemDefault()).toLocalDateTime().minusMinutes(5)
        );

        // Assert
        assertThat(hasDuplicate).isTrue();
    }

    @Test
    @DisplayName("Não deve detectar pedido duplicado fora da janela de tempo")
    void shouldNotDetectDuplicateOrderOutsideTimeWindow() {
        // Arrange
        var oldTime = Instant.parse("2024-01-01T10:00:00Z");
        TestConfig.setClock(Clock.fixed(oldTime, ZoneId.systemDefault()));

        var oldOrder = Order.create(
            "customer123",
            List.of("prod1", "prod2"),
            BigDecimal.valueOf(100.0),
            Clock.fixed(oldTime, ZoneId.systemDefault())
        );
        
        orderRepository.save(oldOrder);

        // Avança o tempo em 10 minutos
        var currentTime = oldTime.plusSeconds(10 * 60);
        TestConfig.setClock(Clock.fixed(currentTime, ZoneId.systemDefault()));

        // Act
        var hasDuplicate = orderRepository.existsByCustomerIdAndCreatedAtAfter(
            oldOrder.customerId(),
            currentTime.atZone(ZoneId.systemDefault()).toLocalDateTime().minusMinutes(5)
        );

        // Assert
        assertThat(hasDuplicate).isFalse();
    }

    @Test
    @DisplayName("Não deve detectar pedido duplicado para cliente diferente")
    void shouldNotDetectDuplicateOrderForDifferentCustomer() {
        // Arrange
        var now = Instant.parse("2024-01-01T10:00:00Z");
        TestConfig.setClock(Clock.fixed(now, ZoneId.systemDefault()));

        var order = Order.create(
            "customer1",
            List.of("prod1", "prod2"),
            BigDecimal.valueOf(100.0),
            Clock.fixed(now, ZoneId.systemDefault())
        );
        
        orderRepository.save(order);

        // Act
        var hasDuplicate = orderRepository.existsByCustomerIdAndCreatedAtAfter(
            "customer2",
            now.atZone(ZoneId.systemDefault()).toLocalDateTime().minusMinutes(5)
        );

        // Assert
        assertThat(hasDuplicate).isFalse();
    }
}
