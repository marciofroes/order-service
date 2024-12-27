package com.lopes.order.application.usecase;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.infrastructure.metrics.OrderMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderMetrics metrics;

    public record GetOrderInput(
        String orderId
    ) {}

    public Order execute(GetOrderInput input) {
        try {
            log.debug("Getting order: {}", input.orderId());
            var start = System.currentTimeMillis();

            var order = orderRepository.findById(input.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + input.orderId()));

            metrics.recordOrderProcessingTime(System.currentTimeMillis() - start);
            return order;
        } catch (Exception e) {
            metrics.incrementOrderError();
            log.error("Error getting order: {}", input.orderId(), e);
            throw e;
        }
    }
}
