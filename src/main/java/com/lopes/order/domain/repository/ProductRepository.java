package com.lopes.order.domain.repository;

import com.lopes.order.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAllById(List<String> ids);
    Optional<Product> findById(String id);
    Product save(Product product);
    void deleteById(String id);
}
