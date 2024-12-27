package com.lopes.order.infrastructure.persistence.repository;

import com.lopes.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MongoOrderRepository extends MongoRepository<OrderEntity, String> {
    boolean existsByCustomerIdAndCreatedAtAfter(String customerId, LocalDateTime after);
    
    Optional<OrderEntity> findFirstByCustomerIdAndCreatedAtGreaterThanOrderByCreatedAtDesc(
        String customerId, LocalDateTime since);
}
