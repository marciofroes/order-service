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
public class ProcessOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderMetrics metrics;

    public Order execute(ProcessOrderInput input) {
        try {
            log.debug("Processing order: {}", input.orderId());
            var start = System.currentTimeMillis();

            var order = orderRepository.findById(input.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + input.orderId()));

            var processedOrder = order.process();
            var savedOrder = orderRepository.save(processedOrder);

            metrics.recordOrderProcessingTime(System.currentTimeMillis() - start);
            return savedOrder;
        } catch (Exception e) {
            metrics.incrementOrderError();
            log.error("Error processing order: {}", input.orderId(), e);
            throw e;
        }
    }

    public record ProcessOrderInput(String orderId) {}
}
