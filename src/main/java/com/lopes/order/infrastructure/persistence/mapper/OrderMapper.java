package com.lopes.order.infrastructure.persistence.mapper;

import com.lopes.order.domain.model.Order;
import com.lopes.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderEntity toEntity(Order order) {
        return OrderEntity.builder()
            .id(order.id())
            .customerId(order.customerId())
            .productIds(order.productIds())
            .totalValue(order.totalValue())
            .status(order.status().name())
            .createdAt(order.createdAt())
            .build();
    }

    public Order toDomain(OrderEntity entity) {
        return new Order(
            entity.getId(),
            entity.getCustomerId(),
            entity.getProductIds(),
            entity.getTotalValue(),
            entity.getOrderStatus(),
            entity.getCreatedAt()
        );
    }
}
