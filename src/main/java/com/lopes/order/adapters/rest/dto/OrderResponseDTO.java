package com.lopes.order.adapters.rest.dto;

import com.lopes.order.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class OrderResponseDTO {
    private final String id;
    private final String customerId;
    private final List<String> productIds;
    private final BigDecimal totalValue;
    private final OrderStatus status;
}
