package com.lopes.order.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Order(
    String id,
    String customerId,
    List<String> productIds,
    BigDecimal totalValue,
    OrderStatus status
) {
    public Order {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve ter pelo menos um produto");
        }
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor total deve ser maior ou igual a zero");
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        // Criando uma cópia imutável da lista de produtos
        productIds = List.copyOf(productIds);
    }
}
