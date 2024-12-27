package com.lopes.order.adapters.rest;

import com.lopes.order.adapters.rest.dto.OrderRequestDTO;
import com.lopes.order.application.usecase.CreateOrderUseCase;
import com.lopes.order.application.usecase.GetOrderUseCase;
import com.lopes.order.application.usecase.ProcessOrderUseCase;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private GetOrderUseCase getOrderUseCase;

    @Mock
    private ProcessOrderUseCase processOrderUseCase;

    @InjectMocks
    private OrderController orderController;

    private static final String ORDER_ID = "order123";
    private static final String CUSTOMER_ID = "customer123";
    private static final List<String> PRODUCT_IDS = List.of("prod1", "prod2");
    private static final BigDecimal TOTAL_VALUE = BigDecimal.valueOf(100.0);
    private static final Clock FIXED_CLOCK = Clock.fixed(
        Instant.parse("2024-01-01T10:00:00Z"), 
        ZoneId.systemDefault()
    );

    @Nested
    @DisplayName("Testes de Criação de Pedido")
    class CreateOrderTests {
        
        @Test
        @DisplayName("Deve criar pedido com sucesso")
        void shouldCreateOrderSuccessfully() {
            // Arrange
            var request = new OrderRequestDTO(CUSTOMER_ID, PRODUCT_IDS);
            var order = Order.create(CUSTOMER_ID, PRODUCT_IDS, TOTAL_VALUE, FIXED_CLOCK);

            when(createOrderUseCase.execute(any(CreateOrderUseCase.CreateOrderInput.class)))
                .thenReturn(order);

            // Act
            var response = orderController.createOrder(request);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getBody().productIds()).isEqualTo(PRODUCT_IDS);
            assertThat(response.getBody().status()).isEqualTo(OrderStatus.CREATED);
        }
    }

    @Nested
    @DisplayName("Testes de Consulta de Pedido")
    class GetOrderTests {

        @Test
        @DisplayName("Deve retornar pedido quando existir")
        void shouldReturnOrderWhenExists() {
            // Arrange
            var order = Order.create(CUSTOMER_ID, PRODUCT_IDS, TOTAL_VALUE, FIXED_CLOCK);

            when(getOrderUseCase.execute(any(GetOrderUseCase.GetOrderInput.class)))
                .thenReturn(order);

            // Act
            var response = orderController.getOrder(ORDER_ID);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getBody().productIds()).isEqualTo(PRODUCT_IDS);
            assertThat(response.getBody().status()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        @DisplayName("Deve retornar 404 quando pedido não existir")
        void shouldReturn404WhenOrderNotFound() {
            // Arrange
            when(getOrderUseCase.execute(any(GetOrderUseCase.GetOrderInput.class)))
                .thenThrow(new RuntimeException("Order not found"));

            // Act
            var response = orderController.getOrder(ORDER_ID);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Testes de Processamento de Pedido")
    class ProcessOrderTests {

        @Test
        @DisplayName("Deve processar pedido com sucesso")
        void shouldProcessOrderSuccessfully() {
            // Arrange
            var order = Order.create(CUSTOMER_ID, PRODUCT_IDS, TOTAL_VALUE, FIXED_CLOCK).process();

            when(processOrderUseCase.execute(any(ProcessOrderUseCase.ProcessOrderInput.class)))
                .thenReturn(order);

            // Act
            var response = orderController.processOrder(ORDER_ID);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getBody().productIds()).isEqualTo(PRODUCT_IDS);
            assertThat(response.getBody().status()).isEqualTo(OrderStatus.PROCESSED);
        }
    }
}
