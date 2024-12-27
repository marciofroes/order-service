package com.lopes.order.application.usecase;

import com.lopes.order.domain.exception.DuplicateOrderException;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.domain.port.ProductService;
import com.lopes.order.infrastructure.metrics.OrderMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final Clock clock;
    private final OrderMetrics metrics;

    public Order execute(CreateOrderInput input) {
        try {
            log.debug("Creating order for customer: {}", input.customerId());
            var start = System.currentTimeMillis();

            validateNoDuplicateOrder(input.customerId());
            var totalValue = productService.calculateTotal(input.productIds());
            var order = Order.create(input.customerId(), input.productIds(), totalValue, clock);
            var savedOrder = orderRepository.save(order);

            metrics.incrementOrderCreated();
            metrics.recordOrderProcessingTime(System.currentTimeMillis() - start);

            log.info("Order created successfully. Order ID: {}", savedOrder.id());
            return savedOrder;
        } catch (Exception e) {
            metrics.incrementOrderError();
            log.error("Error creating order for customer: {}", input.customerId(), e);
            throw e;
        }
    }

    private void validateNoDuplicateOrder(String customerId) {
        var fiveMinutesAgo = LocalDateTime.now(clock).minusMinutes(5);
        if (orderRepository.existsByCustomerIdAndCreatedAtAfter(customerId, fiveMinutesAgo)) {
            throw new DuplicateOrderException("Customer already has an order in the last 5 minutes");
        }
    }

    public record CreateOrderInput(String customerId, List<String> productIds) {}
}
