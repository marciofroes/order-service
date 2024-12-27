package com.lopes.order.domain.event;

import com.lopes.order.domain.model.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderEvent {
    private final String eventId;
    private final String eventType;
    private final LocalDateTime eventTimestamp;
    private final OrderEventData data;

    public static OrderEvent orderCreated(String orderId, String customerId, List<String> productIds, 
            BigDecimal totalValue, OrderStatus status, LocalDateTime createdAt) {
        return new OrderEvent(
            java.util.UUID.randomUUID().toString(),
            "ORDER_CREATED",
            LocalDateTime.now(),
            new OrderEventData(orderId, customerId, productIds, totalValue, status, createdAt)
        );
    }

    public static OrderEvent orderStatusChanged(String orderId, OrderStatus newStatus) {
        return new OrderEvent(
            java.util.UUID.randomUUID().toString(),
            "ORDER_STATUS_CHANGED",
            LocalDateTime.now(),
            new OrderEventData(orderId, null, null, null, newStatus, null)
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class OrderEventData {
        private final String orderId;
        private final String customerId;
        private final List<String> productIds;
        private final BigDecimal totalValue;
        private final OrderStatus status;
        private final LocalDateTime createdAt;
    }
}
