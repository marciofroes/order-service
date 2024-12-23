package com.lopes.order.infrastructure.persistence;

import com.lopes.order.domain.model.Product;
import com.lopes.order.domain.repository.ProductRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoProductRepository extends MongoRepository<Product, String>, ProductRepository {
}
