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
public class GetOrderUseCase {
    private final OrderRepository orderRepository;

    @Builder
    public record GetOrderInput(
        String orderId
    ) {}

    @Builder
    public record GetOrderOutput(
        String id,
        String customerId,
        List<String> productIds,
        BigDecimal totalValue,
        OrderStatus status
    ) {
        public static GetOrderOutput fromOrder(Order order) {
            return new GetOrderOutput(
                order.id(),
                order.customerId(),
                order.productIds(),
                order.totalValue(),
                order.status()
            );
        }
    }

    public GetOrderOutput execute(GetOrderInput input) {
        return orderRepository.findById(input.orderId())
            .map(GetOrderOutput::fromOrder)
            .orElseThrow(() -> new RuntimeException("Order not found: " + input.orderId()));
    }
}
