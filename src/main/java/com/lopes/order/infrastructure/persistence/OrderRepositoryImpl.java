package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.infrastructure.persistence.entity.OrderEntity;
import com.lopes.order.infrastructure.persistence.repository.JpaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpaOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaOrderRepository.deleteById(id);
    }

    private OrderEntity toEntity(Order order) {
        return OrderEntity.builder()
            .id(order.id())
            .customerId(order.customerId())
            .productIds(order.productIds())
            .totalValue(order.totalValue())
            .status(order.status())
            .build();
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(
            entity.getId(),
            entity.getCustomerId(),
            entity.getProductIds(),
            entity.getTotalValue(),
            entity.getStatus()
        );
    }
}
