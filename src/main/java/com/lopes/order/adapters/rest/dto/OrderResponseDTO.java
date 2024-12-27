package com.lopes.order.adapters.rest.dto;

import com.lopes.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    String id,
    String customerId,
    List<String> productIds,
    BigDecimal totalValue,
    OrderStatus status,
    LocalDateTime createdAt
) {}
