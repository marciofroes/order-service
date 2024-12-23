package com.lopes.order.application.usecase;

import com.lopes.order.domain.model.Order;
import com.lopes.order.infrastructure.persistence.OrderRepository;

public class ProcessOrderUseCase {
    private final OrderRepository orderRepository;

    public ProcessOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void processarPedido(Order order) {
        // Lógica para salvar o pedido no banco.
        orderRepository.save(order);
    }
}
