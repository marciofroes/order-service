package com.lopes.order.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderRequestDTO {
    @NotBlank(message = "Customer ID is required")
    private final String customerId;

    @NotEmpty(message = "Order must have at least one product")
    private final List<String> productIds;
}
