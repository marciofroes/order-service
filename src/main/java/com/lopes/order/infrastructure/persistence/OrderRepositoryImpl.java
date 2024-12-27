package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.event.OrderEvent;
import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.infrastructure.messaging.OrderEventService;
import com.lopes.order.infrastructure.persistence.entity.OrderEntity;
import com.lopes.order.infrastructure.persistence.mapper.OrderMapper;
import com.lopes.order.infrastructure.persistence.repository.MongoOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final MongoOrderRepository repository;
    private final OrderMapper mapper;
    private final OrderEventService eventService;
    private static final String CACHE_NAME = "orders";

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#order.customerId")
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = repository.save(entity);
        Order savedOrder = mapper.toDomain(savedEntity);
        
        // Publica evento de criação de pedido
        eventService.publishOrderEvent(OrderEvent.orderCreated(
            savedOrder.id(),
            savedOrder.customerId(),
            savedOrder.productIds(),
            savedOrder.totalValue(),
            savedOrder.status(),
            savedOrder.createdAt()
        ));
        
        return savedOrder;
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByCustomerIdAndCreatedAtAfter(String customerId, LocalDateTime after) {
        return repository.existsByCustomerIdAndCreatedAtAfter(customerId, after);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#customerId")
    public Optional<Order> findLatestByCustomerId(String customerId, LocalDateTime since) {
        return repository.findFirstByCustomerIdAndCreatedAtGreaterThanOrderByCreatedAtDesc(
                customerId, since)
            .map(mapper::toDomain);
    }
}
