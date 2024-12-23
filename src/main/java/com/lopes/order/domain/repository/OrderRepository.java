package com.lopes.order.domain.repository;

import com.lopes.order.domain.model.Order;
import java.util.Optional;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    void deleteById(String id);
    boolean existePedidoClienteProdutos(String idCliente, List<String> idsProdutos);
}
