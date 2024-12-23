package com.lopes.order.application.usecase;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import com.lopes.order.domain.port.OrderRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessOrderUseCase {
    private final OrderRepository orderRepository;

    @Builder
    public record ProcessOrderInput(
        String orderId,
        OrderStatus newStatus
    ) {}

    @Builder
    public record ProcessOrderOutput(
        String id,
        String customerId,
        List<String> productIds,
        BigDecimal totalValue,
        OrderStatus status
    ) {
        public static ProcessOrderOutput fromOrder(Order order) {
            return new ProcessOrderOutput(
                order.id(),
                order.customerId(),
                order.productIds(),
                order.totalValue(),
                order.status()
            );
        }
    }

    public ProcessOrderOutput execute(ProcessOrderInput input) {
        Order order = orderRepository.findById(input.orderId())
            .orElseThrow(() -> new RuntimeException("Order not found: " + input.orderId()));

        validateStatusTransition(order.status(), input.newStatus());

        Order updatedOrder = new Order(
            order.id(),
            order.customerId(),
            order.productIds(),
            order.totalValue(),
            input.newStatus()
        );

        Order savedOrder = orderRepository.save(updatedOrder);
        return ProcessOrderOutput.fromOrder(savedOrder);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        var isValid = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!isValid) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
}
