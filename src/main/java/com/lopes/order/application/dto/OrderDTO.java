package com.lopes.order.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
    String id,
    String customerId,
    List<String> productIds,
    BigDecimal totalValue,
    String status
) {}
