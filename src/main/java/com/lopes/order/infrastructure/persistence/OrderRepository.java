package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}

