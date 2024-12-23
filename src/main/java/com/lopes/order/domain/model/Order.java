package com.lopes.order.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Order(
    String id,
    String customerId,
    List<String> productIds,
    BigDecimal totalValue,
    OrderStatus status
) {}
