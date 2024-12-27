package com.lopes.order.infrastructure.cache;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OrderCacheIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCacheOrderQueries() {
        // Arrange
        var customerId = "customer123";
        var fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.systemDefault());
        var order = Order.create(
            customerId,
            List.of("prod1"),
            BigDecimal.TEN,
            fixedClock
        );

        // Act & Assert
        // Primeira chamada - deve ir ao banco
        orderRepository.save(order);
        var firstCall = orderRepository.findLatestByCustomerId(customerId, 
            LocalDateTime.now(fixedClock).minusMinutes(5));
        assertThat(firstCall).isPresent();

        // Segunda chamada - deve vir do cache
        var secondCall = orderRepository.findLatestByCustomerId(customerId, 
            LocalDateTime.now(fixedClock).minusMinutes(5));
        assertThat(secondCall).isPresent();
        assertThat(secondCall.get()).isEqualTo(firstCall.get());

        // Após salvar novo pedido, cache deve ser invalidado
        var laterClock = Clock.fixed(Instant.parse("2024-01-01T10:01:00Z"), ZoneId.systemDefault());
        var newOrder = Order.create(
            customerId,
            List.of("prod2"),
            BigDecimal.valueOf(20),
            laterClock
        );
        orderRepository.save(newOrder);

        // Terceira chamada - deve ir ao banco novamente
        var thirdCall = orderRepository.findLatestByCustomerId(customerId, 
            LocalDateTime.now(fixedClock).minusMinutes(5));
        assertThat(thirdCall).isPresent();
        assertThat(thirdCall.get()).isNotEqualTo(firstCall.get());
        assertThat(thirdCall.get()).isEqualTo(newOrder);
    }
}
