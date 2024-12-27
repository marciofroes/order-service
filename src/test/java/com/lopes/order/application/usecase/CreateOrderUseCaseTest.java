package com.lopes.order.application.usecase;

import com.lopes.order.domain.exception.DuplicateOrderException;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.domain.port.ProductService;
import com.lopes.order.infrastructure.metrics.OrderMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderMetrics metrics;

    private Clock clock;
    private CreateOrderUseCase createOrderUseCase;

    private static final String CUSTOMER_ID = "customer123";
    private static final List<String> PRODUCT_IDS = List.of("prod1", "prod2");
    private static final BigDecimal TOTAL_VALUE = BigDecimal.valueOf(100.0);

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.systemDefault());
        createOrderUseCase = new CreateOrderUseCase(orderRepository, productService, clock, metrics);
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        var input = new CreateOrderUseCase.CreateOrderInput(CUSTOMER_ID, PRODUCT_IDS);
        
        when(productService.calculateTotal(PRODUCT_IDS)).thenReturn(TOTAL_VALUE);
        when(orderRepository.existsByCustomerIdAndCreatedAtAfter(eq(CUSTOMER_ID), any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var order = createOrderUseCase.execute(input);

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(order.productIds()).isEqualTo(PRODUCT_IDS);
        assertThat(order.totalValue()).isEqualTo(TOTAL_VALUE);
        
        verify(metrics).incrementOrderCreated();
        verify(metrics).recordOrderProcessingTime(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente já tem pedido recente")
    void shouldThrowExceptionWhenCustomerHasRecentOrder() {
        // Arrange
        var input = new CreateOrderUseCase.CreateOrderInput(CUSTOMER_ID, PRODUCT_IDS);
        
        when(orderRepository.existsByCustomerIdAndCreatedAtAfter(eq(CUSTOMER_ID), any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> createOrderUseCase.execute(input))
            .isInstanceOf(DuplicateOrderException.class)
            .hasMessageContaining("Customer already has an order in the last 5 minutes");
            
        verify(metrics).incrementOrderError();
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço de produtos falha")
    void shouldThrowExceptionWhenProductServiceFails() {
        // Arrange
        var input = new CreateOrderUseCase.CreateOrderInput(CUSTOMER_ID, PRODUCT_IDS);
        
        when(orderRepository.existsByCustomerIdAndCreatedAtAfter(eq(CUSTOMER_ID), any())).thenReturn(false);
        when(productService.calculateTotal(PRODUCT_IDS)).thenThrow(new RuntimeException("Product service error"));

        // Act & Assert
        assertThatThrownBy(() -> createOrderUseCase.execute(input))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Product service error");
            
        verify(metrics).incrementOrderError();
    }
}
