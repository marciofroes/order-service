package com.lopes.order.infrastructure.persistence.repository;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.infrastructure.persistence.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final MongoOrderRepository mongoRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        var entity = mapper.toEntity(order);
        var savedEntity = mongoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return mongoRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return mongoRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsByCustomerIdAndCreatedAtAfter(String customerId, LocalDateTime after) {
        return mongoRepository.existsByCustomerIdAndCreatedAtAfter(customerId, after);
    }

    @Override
    public Optional<Order> findLatestByCustomerId(String customerId, LocalDateTime since) {
        return mongoRepository.findFirstByCustomerIdAndCreatedAtGreaterThanOrderByCreatedAtDesc(
            customerId, since
        ).map(mapper::toDomain);
    }
}
