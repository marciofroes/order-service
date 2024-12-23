package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.repository.OrderRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoOrderRepository extends MongoRepository<Order, String>, OrderRepository {
}
