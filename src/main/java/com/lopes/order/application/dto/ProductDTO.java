package com.lopes.order.application.dto;

import java.math.BigDecimal;

public record ProductDTO(
    String id,
    String name,
    BigDecimal price
) {}
