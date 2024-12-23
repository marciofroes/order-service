package com.lopes.order.domain.model;

import java.math.BigDecimal;

public record Product(
    String id,
    String name,
    BigDecimal price
) {
    public Product {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }
    }
}
