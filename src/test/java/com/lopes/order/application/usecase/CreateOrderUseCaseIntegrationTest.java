package com.lopes.order.application.usecase;

import com.lopes.order.domain.exception.DuplicateOrderException;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import com.lopes.order.domain.port.ProductService;
import com.lopes.order.infrastructure.metrics.OrderMetrics;
import com.lopes.order.infrastructure.persistence.mapper.OrderMapper;
import com.lopes.order.infrastructure.persistence.repository.MongoOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class CreateOrderUseCaseIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @TestConfiguration
    static class TestConfig {
        private static Clock clock;
        private static ProductService productService;
        private static OrderMetrics metrics;

        static void setClock(Clock newClock) {
            clock = newClock;
        }

        static void setProductService(ProductService service) {
            productService = service;
        }

        static void setMetrics(OrderMetrics newMetrics) {
            metrics = newMetrics;
        }

        @Bean
        @Primary
        Clock clock() {
            return clock;
        }

        @Bean
        @Primary
        ProductService productService() {
            return productService;
        }

        @Bean
        @Primary
        OrderMetrics orderMetrics() {
            return metrics;
        }
    }

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private MongoOrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    private static final String CUSTOMER_ID = "customer123";
    private static final List<String> PRODUCT_IDS = List.of("prod1", "prod2");
    private static final BigDecimal TOTAL_VALUE = BigDecimal.valueOf(100.0);
    private static final Clock FIXED_CLOCK = Clock.fixed(
        Instant.parse("2024-01-01T10:00:00Z"),
        ZoneId.systemDefault()
    );

    @BeforeEach
    void setUp() {
        TestConfig.setClock(FIXED_CLOCK);
        var productService = mock(ProductService.class);
        when(productService.calculateTotal(any())).thenReturn(TOTAL_VALUE);
        TestConfig.setProductService(productService);

        var metrics = mock(OrderMetrics.class);
        TestConfig.setMetrics(metrics);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        var input = new CreateOrderUseCase.CreateOrderInput(CUSTOMER_ID, PRODUCT_IDS);

        // Act
        var order = createOrderUseCase.execute(input);

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(order.productIds()).isEqualTo(PRODUCT_IDS);
        assertThat(order.totalValue()).isEqualTo(TOTAL_VALUE);
        assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.createdAt()).isNotNull();

        // Verify persistence
        var savedEntity = orderRepository.findById(order.id()).orElseThrow();
        assertThat(savedEntity.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(savedEntity.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente já tem pedido recente")
    void shouldThrowExceptionWhenCustomerHasRecentOrder() {
        // Arrange
        var input = new CreateOrderUseCase.CreateOrderInput(CUSTOMER_ID, PRODUCT_IDS);
        var existingOrder = Order.create(CUSTOMER_ID, PRODUCT_IDS, TOTAL_VALUE, FIXED_CLOCK);
        var existingEntity = orderMapper.toEntity(existingOrder);
        orderRepository.save(existingEntity);

        // Act & Assert
        assertThatThrownBy(() -> createOrderUseCase.execute(input))
            .isInstanceOf(DuplicateOrderException.class)
            .hasMessageContaining("Customer already has an order in the last 5 minutes");
    }
}
